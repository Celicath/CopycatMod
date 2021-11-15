package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.DualImageCard;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.patches.DualImageCardPatch;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.CutThroughFate;
import com.megacrit.cardcrawl.cards.red.PommelStrike;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HybridStrike extends CustomCard implements DualImageCard {
	public static final String DESCRIPTION = CardCrawlGame.languagePack.getCardStrings(PommelStrike.ID).DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = CardCrawlGame.languagePack.getCardStrings(CutThroughFate.ID).DESCRIPTION;
	private static final String RAW_ID = "HybridStrike";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final String RAW_ID2 = "HybridStrike2";
	public static final String IMG_UPGRADE = CopycatModMain.GetCardPath(RAW_ID2);
	public static final String IMG_UPGRADE_PORTRAIT = CopycatModMain.GetCardPath(RAW_ID2 + "_p");
	private static final int COST = 1;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 9;
	private static final int MAGIC = 1;
	private static final int UPGRADE_BONUS = 2;

	public HybridStrike() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = MAGIC;
		baseDamage = POWER;
		tags.add(CardTags.STRIKE);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
		if (upgraded) {
			this.addToBot(new ScryAction(magicNumber));
			addToBot(new DrawCardAction(p, 1));
		} else {
			addToBot(new DrawCardAction(p, magicNumber));
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new HybridStrike();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
			rawDescription = UPGRADE_DESCRIPTION;
			initializeDescription();
			textureImg = IMG_UPGRADE;
			loadCardImage(textureImg);
		}
	}

	@Override
	protected Texture getPortraitImage() {
		if (upgraded) {
			DualImageCardPatch.portraitUpgraded = true;
			try {
				return ImageMaster.loadImage(IMG_UPGRADE_PORTRAIT);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			DualImageCardPatch.portraitUpgraded = false;
			return super.getPortraitImage();
		}
	}

	@Override
	public boolean viewUpgradedImage() {
		return upgraded;
	}
}
