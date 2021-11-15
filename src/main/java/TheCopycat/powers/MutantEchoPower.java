package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.ManipulateIntentAction;
import TheCopycat.cards.monster.AbstractMonsterCard;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.friendlyminions.MirrorMinion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class MutantEchoPower extends AbstractPower {
	public static final String RAW_ID = "MutantEchoPower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
		ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 128)), 0, 0, 84, 84);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
		ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 48)), 0, 0, 32, 32);

	public MutantEchoPower(AbstractMonster owner) {
		this.name = powerStrings.NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.updateDescription();
		this.region128 = IMG128;
		this.region48 = IMG48;
	}

	public void updateDescription() {
		description = DESCRIPTIONS[0];
	}

	public void onAfterUseCard(AbstractCard card, UseCardAction action) {
		if (card instanceof AbstractMonsterCard && owner instanceof AbstractMonster) {
			flash();
			AbstractMonster user = owner instanceof MirrorMinion ? ((MirrorMinion) owner).origMonster : (AbstractCopycatMinion) owner;
			addToBot(new ManipulateIntentAction(user, (AbstractMonsterCard) card, true));
		}
	}
}
