package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.monster.AbstractMonsterCard;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class MonstrousPlanPower extends AbstractPower {
	public static final String RAW_ID = "MonstrousPlanPower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 128)), 0, 0, 87, 86);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 48)), 0, 0, 32, 32);

	public MonstrousPlanPower(AbstractCreature owner) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.updateDescription();
		this.type = AbstractPower.PowerType.BUFF;
		this.region128 = IMG128;
		this.region48 = IMG48;
	}

	@Override
	public void updateDescription() {
		description = DESCRIPTIONS[0];
	}

	@Override
	public void atEndOfTurn(boolean isPlayer) {
		boolean flashed = false;
		if (isPlayer) {
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				if (c instanceof AbstractMonsterCard && !c.isEthereal) {
					c.retain = true;
					if (!flashed) {
						flash();
						flashed = true;
					}
				}
			}
		}
	}
}
