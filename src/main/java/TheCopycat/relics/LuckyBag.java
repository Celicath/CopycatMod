package TheCopycat.relics;

import TheCopycat.CopycatModMain;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class LuckyBag extends CustomRelic {

	private static final String RAW_ID = "LuckyBag";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	public static final String IMG = CopycatModMain.GetRelicPath(RAW_ID);
	public static final String OUTLINE = CopycatModMain.GetRelicOutlinePath(RAW_ID);

	boolean usedUp;

	public LuckyBag() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.STARTER, LandingSound.FLAT);

		tips.clear();
		tips.add(new PowerTip(name, description));
		tips.add(new PowerTip(DESCRIPTIONS[1], DESCRIPTIONS[2] + " NL " + DESCRIPTIONS[3] + " NL " + DESCRIPTIONS[4] + " NL " + DESCRIPTIONS[5]));
		initializeTips();
	}

	@Override
	public void atPreBattle() {
		usedUp = false;
		if (!this.pulse) {
			this.beginPulse();
			this.pulse = true;
		}
	}

	@Override
	public void justEnteredRoom(AbstractRoom room) {
		this.grayscale = false;
	}

	@Override
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (!usedUp) {
			flash();
			addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
			switch (card.type) {
				case ATTACK:
					addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 2), 2));
					break;
				case SKILL:
					addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new PlatedArmorPower(AbstractDungeon.player, 4), 4));
					break;
				case POWER:
					addToBot(new GainEnergyAction(2));
					break;
				default:
					AbstractDungeon.player.increaseMaxHp(4, true);
					break;
			}
			usedUp = true;
			pulse = false;
			grayscale = true;
		}
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new LuckyBag();
	}
}
