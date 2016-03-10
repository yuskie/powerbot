package montezuma.script.task;

import java.awt.Graphics;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

/**
 * Chop a tree based on the name of a GameObject "Tree"
 * 
 * @author Robby
 *
 */
public class TalkToGuideTask extends Task<ClientContext> {

	public String guideName;

	private Npc guide;

	public TalkToGuideTask(ClientContext ctx, String guideName) {
		super(ctx);
		this.guideName = guideName;
		this.guide = null;
	}

	@Override
	public boolean activate() {
		return !ctx.npcs.select().name(guideName).isEmpty() && !ctx.players.local().inMotion();
	}

	@Override
	public void execute() {

		guide = ctx.npcs.nearest().poll();

		if (guide.inViewport()) {

			if (ctx.chat.canContinue()) {
				Condition.sleep(Random.nextInt(350, 500));

				ctx.chat.clickContinue(false);
			} else if (ctx.chat.chatting()) {
				ctx.chat.select().text("Yes.").poll().select();
			} else {
				guide.interact("Talk-To");
			}

		} else {
			ctx.movement.step(guide);
			ctx.camera.turnTo(guide);
		}

	}

	@Override
	public void paint(Graphics g1) {
		guide.draw(g1);
	}

	@Override
	public boolean draw() {
		return guide != null;
	}

}
