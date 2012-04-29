package org.powerbot.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.util.StringUtil;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.Resources;
import org.powerbot.util.io.SecureStore;

/**
 * @author Paris
 */
public final class NetworkAccount {
	private static NetworkAccount instance = null;
	private final static String FILENAME = "signin.ini";
	private Account account;

	private NetworkAccount() {
		try {
			final InputStream is = SecureStore.getInstance().read(FILENAME);
			if (is != null) {
				parseResponse(is);
			}
		} catch (final IOException ignored) {
		} catch (final GeneralSecurityException ignored) {
		}
	}

	public static NetworkAccount getInstance() {
		if (instance == null) {
			instance = new NetworkAccount();
		}
		return instance;
	}

	public boolean isLoggedIn() {
		return account != null && account.getID() != 0;
	}

	public boolean isVIP() {
		return account != null && account.isVIP();
	}

	public Account getAccount() {
		return account;
	}

	public boolean login(final String username, final String password) throws IOException {
		InputStream is;
		try {
			is = Resources.openHttpStream("signin", StringUtil.urlEncode(username), StringUtil.urlEncode(password));
		} catch (final NullPointerException ignored) {
			return false;
		}
		final boolean success = parseResponse(is) && isLoggedIn();
		if (success) {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IniParser.serialise(account.getMap(), bos);
			bos.close();
			try {
				SecureStore.getInstance().write(FILENAME, bos.toByteArray());
			} catch (final GeneralSecurityException ignored) {
			}
		}
		return success;
	}

	private boolean parseResponse(final InputStream is) throws IOException {
		final Map<String, Map<String, String>> data = IniParser.deserialise(is);
		if (data == null || data.size() == 0 || !data.containsKey("auth")) {
			return false;
		}
		final Map<String, String> auth = data.get("auth");
		final int id = Integer.parseInt(auth.get("member_id"));
		final String[] groups = auth.get("groups").split(",");
		final int[] groupIDs = new int[groups.length];
		for (int i = 0; i < groups.length; i++) {
			groupIDs[i] = Integer.parseInt(groups[i]);
		}
		account = new Account(id, auth.get("auth"), auth.get("name"), auth.get("display"), auth.get("email"), groupIDs);
		return true;
	}

	public void logout() {
		account = null;
		try {
			SecureStore.getInstance().delete(FILENAME);
		} catch (final IOException ignored) {
		} catch (final GeneralSecurityException ignored) {
		}
	}

	public final class Account {
		private final int id;
		private final String auth, name, display, email;
		private final int[] groups;

		public Account(final int id, final String auth, final String name, final String display, final String email, final int[] groups) {
			this.id = id;
			this.auth = auth;
			this.name = name;
			this.display = display;
			this.email = email;
			this.groups = groups;
		}

		public int getID() {
			return id;
		}

		public String getAuth() {
			return auth;
		}

		public String getName() {
			return name;
		}

		public String getDisplayName() {
			return display;
		}

		public String getEmail() {
			return email;
		}

		public int[] getGroupIDs() {
			return groups;
		}

		public boolean isVIP() {
			final String groups;
			try {
				groups = Resources.getServerData().get("access").get("vip");
			} catch (final Exception ignored) {
				return false;
			}
			for (final String group : groups.split(",")) {
				final int g;
				try {
					g = Integer.parseInt(group);
				} catch (final NumberFormatException ignored) {
					continue;
				}
				for (final int check : this.groups) {
					if (check == g) {
						return true;
					}
				}
			}
			return false;
		}

		public Map<String, Map<String, String>> getMap() {
			final Map<String, String> auth = new HashMap<String, String>();
			auth.put("member_id", Integer.toString(id));
			auth.put("name", name);
			auth.put("display", display);
			auth.put("email", email);
			final StringBuilder groups = new StringBuilder(this.groups.length * 2);
			for (final int group : this.groups) {
				groups.append(',');
				groups.append(Integer.toString(group));
			}
			groups.deleteCharAt(0);
			auth.put("groups", groups.toString());
			final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
			data.put("auth", auth);
			return data;
		}
	}
}
