package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.interfaces.MinionAuraEffect;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DoubleTimePower extends AbstractPower implements MinionAuraEffect {
	public static final String RAW_ID = "DoubleTimePower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 128)), 0, 0, 84, 84);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 48)), 0, 0, 32, 32);

	public DoubleTimePower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.updateDescription();
		this.type = AbstractPower.PowerType.BUFF;
		this.region128 = IMG128;
		this.region48 = IMG48;
	}

	public static void updateMinions() {
		for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
			if (m instanceof AbstractCopycatMinion) {
				((AbstractCopycatMinion) m).moveCountUpdate = true;
			}
		}
	}

	@Override
	public void onInitialApplication() {
		super.onInitialApplication();
		updateMinions();
	}

	@Override
	public void stackPower(int stackAmount) {
		super.stackPower(stackAmount);
		updateMinions();
	}

	@Override
	public void updateDescription() {
		description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
	}
}
