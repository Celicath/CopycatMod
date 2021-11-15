package TheCopycat.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class SuperDelayAction extends AbstractGameAction {
	private int count;
	private final Runnable runnable;

	public SuperDelayAction(Runnable runnable) {
		count = 3;
		this.runnable = runnable;
	}

	@Override
	public void update() {
		if (count > 0) {
			count--;
			AbstractGameAction thisAction = this;
			addToBot(new AbstractGameAction() {
				@Override
				public void update() {
					thisAction.isDone = false;
					addToBot(thisAction);
					isDone = true;
				}
			});
		} else {
			runnable.run();
		}
		isDone = true;
	}
}
