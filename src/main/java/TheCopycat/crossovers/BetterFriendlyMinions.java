package TheCopycat.crossovers;

import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.vfx.CopycatFlashTargetArrowEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import kobting.friendlyminions.patches.PlayerAddFieldsPatch;

import java.util.ArrayList;

public class BetterFriendlyMinions {
	public static AbstractCopycatMinion[] copycatMinions = new AbstractCopycatMinion[4];
	public static Random minionAiRng = new Random();
	public static Random enemyTargetRng = new Random();

	public static float prevPlayerhX;
	public static float prevPlayerhY;
	public static float prevPlayercX;
	public static float prevPlayercY;
	public static Random prevAiRng = null;
	public static ArrayList<AbstractGameAction> origActions;

	public static ArrayList<AbstractMonster> getMinionList() {
		return (AbstractDungeon.player instanceof AbstractPlayerWithMinions) ?
				((AbstractPlayerWithMinions) AbstractDungeon.player).minions.monsters :
				PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player).monsters;
	}

	public static boolean summonCopycatMinion(AbstractCopycatMinion m) {
		if (PlayerAddFieldsPatch.f_maxMinions.get(AbstractDungeon.player) == 1) {
			PlayerAddFieldsPatch.f_maxMinions.set(AbstractDungeon.player, 4);
		}

		int index = -1;
		for (int i = 0; i < copycatMinions.length; i++) {
			if (copycatMinions[i] != null && !copycatMinions[i].isDead) {
				continue;
			}
			index = i;
			break;
		}
		if (index == -1) {
			return false;
		}

		boolean result;
		if (AbstractDungeon.player instanceof AbstractPlayerWithMinions) {
			result = ((AbstractPlayerWithMinions) AbstractDungeon.player).addMinion(m);
		} else {
			result = BasePlayerMinionHelper.addMinion(AbstractDungeon.player, m);
		}
		if (result) {
			copycatMinions[index] = m;
			m.setIndex(index);
		}
		return result;
	}

	/**
	 * Hijacks action queue.
	 *
	 * @param target redirect target
	 */
	public static void hijackActionQueue(AbstractCreature target) {
		prevPlayerhX = AbstractDungeon.player.hb.x;
		prevPlayerhY = AbstractDungeon.player.hb.y;
		prevPlayercX = AbstractDungeon.player.hb.cX;
		prevPlayerhY = AbstractDungeon.player.hb.cY;
		AbstractDungeon.player.hb.move(target.hb.cX, target.hb.cY);

		prevAiRng = AbstractDungeon.aiRng;
		AbstractDungeon.aiRng = BetterFriendlyMinions.minionAiRng;

		origActions = AbstractDungeon.actionManager.actions;
		AbstractDungeon.actionManager.actions = new ArrayList<>();
	}

	/**
	 * Reverts RNG and player hitbox position changed by hijackActionQueue().
	 * Note that this does not revert the actionManager.actions, you need to manually revert them.
	 *
	 * @return if revert was successful
	 */
	public static boolean revertHijack() {
		if (prevAiRng != null) {
			AbstractDungeon.aiRng = prevAiRng;
			AbstractDungeon.player.hb.x = prevPlayerhX;
			AbstractDungeon.player.hb.y = prevPlayerhY;
			AbstractDungeon.player.hb.cX = prevPlayercX;
			AbstractDungeon.player.hb.cY = prevPlayercY;
			prevAiRng = null;
			return true;
		}
		return false;
	}

	/**
	 * call MonsterHelper.switchTarget and shows TargetArrow.
	 */
	public static void switchTarget(AbstractMonster monster, AbstractFriendlyMonster newTarget) {
		if (MonsterHelper.getTarget(monster) != newTarget) {
			MonsterHelper.switchTarget(monster, newTarget);
			AbstractDungeon.effectsQueue.add(new CopycatFlashTargetArrowEffect(monster, newTarget, 0.75f));
		}
	}
}
