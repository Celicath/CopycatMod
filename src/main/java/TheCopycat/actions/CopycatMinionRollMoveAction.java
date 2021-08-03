package TheCopycat.actions;

import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;

public class CopycatMinionRollMoveAction extends RollMoveAction {
	public CopycatMinionRollMoveAction(AbstractMonster monster) {
		super(monster);
	}

	public void update() {
		Random temp = AbstractDungeon.aiRng;
		AbstractDungeon.aiRng = BetterFriendlyMinionsUtils.minionAiRng;
		super.update();
		AbstractDungeon.aiRng = temp;
	}
}
