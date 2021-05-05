package TheCopycat.friendlyminions;

import TheCopycat.actions.RetargetMinionAction;
import TheCopycat.crossovers.BetterFriendlyMinions;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

public abstract class AbstractCopycatMinion extends AbstractFriendlyMonster {
	public int index;
	public boolean substitute = false;

	public AbstractCopycatMinion(String name, String id, int maxHealth, float width, float height, String imgUrl) {
		super(name, id, maxHealth, 0, 0, width, height, imgUrl, 0, 0);
	}

	public void setIndex(int index) {
		this.index = index;
		float offsetX = (index >= 2 ? -1025.0f : -1225.0f) + 100 * index;
		float offsetY = index == 0 || index == 3 ? 100.0f : 300.0f;

		drawX = Settings.WIDTH * 0.75F + offsetX * Settings.xScale;
		drawY = AbstractDungeon.floorY + offsetY * Settings.yScale;
		refreshHitboxLocation();
		refreshIntentHbLocation();
	}

	public abstract void doMove(AbstractMonster target);

	@Override
	public void die() {
		super.die();
		BetterFriendlyMinions.copycatMinions[index] = null;
		addToBot(new RetargetMinionAction());
	}
}
