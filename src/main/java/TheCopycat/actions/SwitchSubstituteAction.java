package TheCopycat.actions;

import TheCopycat.friendlyminions.SubstituteMinion;
import TheCopycat.utils.BaseGamePrivateBusterUtils;
import basemod.ReflectionHacks;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SwitchSubstituteAction extends AbstractGameAction {
	boolean moveSubstituteFront;

	float minion_x, minion_y;
	float player_x, player_y;

	public SwitchSubstituteAction(boolean moveSubstituteFront) {
		duration = startDuration = Settings.ACTION_DUR_FASTER;
		this.moveSubstituteFront = moveSubstituteFront;

		if (!moveSubstituteFront) {
			AbstractDungeon.player.currentBlock += SubstituteMinion.instance.currentBlock;
		}
	}

	public void update() {
		if (duration == startDuration) {
			if (moveSubstituteFront == SubstituteMinion.instance.isFront || moveSubstituteFront && SubstituteMinion.instance.isDead) {
				isDone = true;
				return;
			}

			minion_x = SubstituteMinion.instance.drawX;
			minion_y = SubstituteMinion.instance.drawY;
			player_x = AbstractDungeon.player.drawX;
			player_y = AbstractDungeon.player.drawY;
			SubstituteMinion.instance.isFront = moveSubstituteFront;

			if (moveSubstituteFront) {
				if (AbstractDungeon.player.currentBlock > 0) {
					SubstituteMinion.instance.currentBlock += AbstractDungeon.player.currentBlock;
					ReflectionHacks.setPrivate(SubstituteMinion.instance, AbstractCreature.class, "blockAnimTimer", 0.001f);
					AbstractDungeon.player.loseBlock(true);
				}
			} else {
				SubstituteMinion.instance.currentBlock = 0;
			}
		}
		tickDuration();
		float t = 1.0f - (duration / startDuration);
		if (t > 1) t = 1;
		AbstractDungeon.player.drawX = Interpolation.fade.apply(player_x, minion_x, t);
		AbstractDungeon.player.drawY = Interpolation.fade.apply(player_y, minion_y, t);
		SubstituteMinion.instance.drawX = Interpolation.fade.apply(minion_x, player_x, t);
		SubstituteMinion.instance.drawY = Interpolation.fade.apply(minion_y, player_y, t);
		BaseGamePrivateBusterUtils.refreshHitboxLocation(AbstractDungeon.player);
		BaseGamePrivateBusterUtils.refreshHitboxLocation(SubstituteMinion.instance);
	}
}
