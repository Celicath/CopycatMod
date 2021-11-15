package TheCopycat.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class MagnetAction extends AbstractGameAction {
	private static final float POWER_ICON_PADDING_X = 48.0F * Settings.scale;
	public static String powID;
	public static AbstractPower pow = null;
	public static float targetDX, targetDY;
	public static float t;
	private final AbstractMonster targetMonster;

	public MagnetAction(AbstractMonster m, String powID) {
		actionType = ActionType.WAIT;
		duration = startDuration = Settings.ACTION_DUR_LONG;
		targetMonster = m;
		MagnetAction.powID = powID;
	}

	public static void calcTargetDelta(float x, float y) {
		AbstractPlayer p = AbstractDungeon.player;
		int index = 0;
		for (; index < p.powers.size(); index++) {
			if (p.powers.get(index).ID.equals(powID)) {
				break;
			}
		}
		targetDX = p.hb.cX - p.hb.width / 2.0F + 10.0F * Settings.scale + index * POWER_ICON_PADDING_X - x;
		targetDY = p.hb.cY - p.hb.height / 2.0F + ReflectionHacks.<Float>getPrivate(p, AbstractCreature.class, "hbYOffset") - 48.0F * Settings.scale - y;
	}

	@Override
	public void update() {
		if (duration == startDuration) {
			if (targetMonster != null) {
				pow = targetMonster.getPower(powID);
				targetDX = targetDY = 0;
			}
			if (pow == null) {
				isDone = true;
				return;
			}
			t = 0;
		}
		tickDuration();
		if (isDone) {
			t = 1;
			addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ArtifactPower(AbstractDungeon.player, pow.amount)));
			addToTop(new RemoveSpecificPowerAction(targetMonster, AbstractDungeon.player, pow) {
				@Override
				public void update() {
					super.update();
					isDone = true;
				}
			});
		} else {
			t = (startDuration - duration) / startDuration;
		}
	}
}
