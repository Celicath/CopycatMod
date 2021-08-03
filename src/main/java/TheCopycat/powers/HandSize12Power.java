package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HandSize12Power extends AbstractPower {
	public static final String RAW_ID = "HandSize12Power";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 128)), 0, 0, 62, 86);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 48)), 0, 0, 24, 33);

	public static final int AMOUNT = 2;

	public HandSize12Power(AbstractCreature owner) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.updateDescription();
		this.type = AbstractPower.PowerType.BUFF;
		this.region128 = IMG128;
		this.region48 = IMG48;
	}

	@Override
	public void onInitialApplication() {
		BaseMod.MAX_HAND_SIZE += AMOUNT;
	}

	@Override
	public void onVictory() {
		BaseMod.MAX_HAND_SIZE -= AMOUNT;
	}

	@Override
	public void onRemove() {
		BaseMod.MAX_HAND_SIZE -= AMOUNT;
	}

	@Override
	public void updateDescription() {
		description = DESCRIPTIONS[0];
	}
}
