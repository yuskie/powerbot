package org.powerbot.bot;

import org.powerbot.client.Callback;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSObjectDef;
import org.powerbot.client.Render;
import org.powerbot.event.EventDispatcher;
import org.powerbot.event.MessageEvent;
import org.powerbot.script.methods.Camera;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.util.math.Vector3f;

public class AbstractCallback implements Callback {
	private final MethodContext ctx;
	private final EventDispatcher dispatcher;

	public AbstractCallback(final Bot bot) {
		ctx = bot.ctx;
		dispatcher = bot.dispatcher;
	}

	@Override
	public void updateRenderInfo(final Render render) {
		ctx.game.updateToolkit(render);
		try {
			ctx.menu.cache();
		} catch (Throwable ignored) {
		}
	}

	@Override
	public void notifyMessage(final int id, final String sender, final String message) {
		dispatcher.dispatch(new MessageEvent(id, sender, message));
	}

	@Override
	public void notifyObjectDefinitionLoad(final RSObjectDef def) {
		ctx.objects.setType(def.getID(), def.getClippingType());
	}

	@Override
	public void updateCamera(final RSInteractableLocation offset, final RSInteractableLocation center) {
		final Camera camera = ctx.camera;
		camera.offset = new Vector3f(offset.getX(), offset.getY(), offset.getZ());
		camera.center = new Vector3f(center.getX(), center.getY(), center.getZ());
	}
}
