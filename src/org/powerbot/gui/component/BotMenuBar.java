package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.event.BotMenuListener;
import org.powerbot.gui.BotAbout;
import org.powerbot.gui.BotAccounts;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotLicense;
import org.powerbot.gui.BotScripts;
import org.powerbot.gui.BotSignin;
import org.powerbot.script.Script;
import org.powerbot.script.internal.ScriptController;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class BotMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = -4186554435386744949L;
	private final BotChrome chrome;
	private final JMenuItem signin, play, stop;

	public BotMenuBar(final BotChrome chrome) {
		this.chrome = chrome;

		final JMenu file = new JMenu(BotLocale.FILE), edit = new JMenu(BotLocale.EDIT), view = new JMenu(BotLocale.VIEW),
				script = new JMenu(BotLocale.SCRIPTS), input = new JMenu(BotLocale.INPUT), help = new JMenu(BotLocale.HELP);

		final JMenuItem newtab = item(BotLocale.NEWWINDOW);
		file.add(newtab);
		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			file.addSeparator();
			file.add(item(BotLocale.EXIT));
		}

		signin = item(BotLocale.SIGNIN);
		signin.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.KEYS)));
		edit.add(signin);
		final JMenuItem accounts = item(BotLocale.ACCOUNTS);
		accounts.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.ADDRESS)));
		edit.add(accounts);

		edit.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final NetworkAccount account = NetworkAccount.getInstance();
				signin.setText(account.isLoggedIn() ? account.getDisplayName() + "..." : BotLocale.SIGNIN);
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		view.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu menu = (JMenu) e.getSource();
				menu.removeAll();
				new BotMenuView(chrome, menu);
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		final ImageIcon[] playIcons = new ImageIcon[]{new ImageIcon(Resources.getImage(Resources.Paths.PLAY)), new ImageIcon(Resources.getImage(Resources.Paths.PAUSE))};
		play = item(BotLocale.PLAYSCRIPT);
		play.setIcon(playIcons[0]);
		script.add(play);
		stop = item(BotLocale.STOPSCRIPT);
		stop.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.STOP)));
		script.add(stop);

		script.addSeparator();
		final JMenu options = new JMenu(BotLocale.OPTIONS);
		options.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.CONFIG)));
		script.add(options);

		options.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu m = (JMenu) e.getSource();
				m.removeAll();

				final ScriptController c = chrome.getBot().controller;
				if (c == null || c.isStopping()) {
					return;
				}

				final Script s = c.getScript();
				if (s == null || !(s instanceof BotMenuListener)) {
					return;
				}

				try {
					((BotMenuListener) s).menuSelected(e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
				final ScriptController c = chrome.getBot().controller;
				if (c == null || c.isStopping()) {
					return;
				}

				final Script s = c.getScript();
				if (s == null || !(s instanceof BotMenuListener)) {
					return;
				}

				try {
					((BotMenuListener) s).menuDeselected(e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
				final ScriptController c = chrome.getBot().controller;
				if (c == null || c.isStopping()) {
					return;
				}

				final Script s = c.getScript();
				if (s == null || !(s instanceof BotMenuListener)) {
					return;
				}

				try {
					((BotMenuListener) s).menuCanceled(e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}
		});

		script.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final ScriptController controller = chrome.getBot().controller;
				final boolean active = controller != null && !controller.isStopping(), running = active && !controller.isSuspended();
				play.setEnabled(chrome.getBot().ctx.getClient() != null && !BotScripts.loading.get());
				play.setText(running ? BotLocale.PAUSESCRIPT : active ? BotLocale.RESUMESCRIPT : BotLocale.PLAYSCRIPT);
				play.setIcon(playIcons[running ? 1 : 0]);
				stop.setEnabled(active);

				if (active) {
					final Script script = controller.getScript();
					options.setEnabled(script != null && script instanceof BotMenuListener);
				} else {
					options.setEnabled(false);
				}
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		input.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu menu = (JMenu) e.getSource();
				if (menu.getItemCount() != 0) {
					menu.removeAll();
				}
				new BotMenuInput(menu);
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			help.add(item(BotLocale.ABOUT));
		}
		help.add(item(BotLocale.LICENSE));
		final JMenuItem web = item(BotLocale.WEBSITE);
		web.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.ICON_SMALL)));
		help.add(web);

		add(file);
		add(edit);
		add(view);
		add(script);
		add(input);
		add(help);
	}

	private JMenuItem item(final String s) {
		final JMenuItem item = new JMenuItem(s);
		item.addActionListener(this);
		return item;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String s = e.getSource() == signin ? BotLocale.SIGNIN : e.getActionCommand();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Tracker.getInstance().trackPage("menu/", s);
			}
		});
		if (s.equals(BotLocale.NEWWINDOW)) {
			Boot.fork();
		} else if (s.equals(BotLocale.EXIT)) {
			chrome.close();
		} else if (s.equals(BotLocale.SIGNIN)) {
			showDialog(Action.SIGNIN);
		} else if (s.equals(BotLocale.ACCOUNTS)) {
			showDialog(Action.ACCOUNTS);
		} else if (s.equals(BotLocale.PLAYSCRIPT) || s.equals(BotLocale.PAUSESCRIPT) || s.equals(BotLocale.RESUMESCRIPT)) {
			scriptPlayPause();
		} else if (s.equals(BotLocale.STOPSCRIPT)) {
			scriptStop();
		} else if (s.equals(BotLocale.ABOUT)) {
			showDialog(Action.ABOUT);
		} else if (s.equals(BotLocale.LICENSE)) {
			showDialog(Action.LICENSE);
		} else if (s.equals(BotLocale.WEBSITE)) {
			BotChrome.openURL(Configuration.URLs.SITE);
		}
	}

	public enum Action {ACCOUNTS, SIGNIN, ABOUT, LICENSE}

	;

	public void showDialog(final Action action) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switch (action) {
				case ACCOUNTS:
					new BotAccounts(chrome);
					break;
				case SIGNIN:
					new BotSignin(chrome);
					break;
				case ABOUT:
					new BotAbout(chrome);
					break;
				case LICENSE:
					new BotLicense(chrome);
					break;
				default:
					break;
				}
			}
		});
	}

	public synchronized void scriptPlayPause() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Bot bot = chrome.getBot();
				final ScriptController script = bot.controller;
				if (script != null && !script.isStopping()) {
					if (script.isSuspended()) {
						script.resume();
					} else {
						script.suspend();
					}
					return;
				}

				if (bot.ctx.getClient() != null) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							new BotScripts(chrome);
						}
					});
				}
			}
		}).start();
	}

	public synchronized void scriptStop() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Bot bot = chrome.getBot();
				final ScriptController controller = bot.controller;
				if (controller != null && !controller.isStopping()) {
					bot.stopScript();
				}
			}
		}).start();
	}
}
