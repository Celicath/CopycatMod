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
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.Leap;
import com.megacrit.cardcrawl.cards.purple.CutThroughFate;
import com.megacrit.cardcrawl.cards.purple.ThirdEye;
import com.megacrit.cardcrawl.cards.red.PommelStrike;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HybridMovement extends CustomCard implements DualImageCard {
	private static final String RAW_ID = "HybridMovement";
	private static final String RAW_ID2 = "HybridMovement2";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	public static final String IMG_UPGRADE = CopycatModMain.GetCardPath(RAW_ID2);
	public static final String IMG_UPGRADE_PORTRAIT = CopycatModMain.GetCardPath(RAW_ID2 + "_p");
	private static final int COST = 1;
	public static final String DESCRIPTION = CardCrawlGame.languagePack.getCardStrings(Leap.ID).DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = CardCrawlGame.languagePack.getCardStrings(ThirdEye.ID).DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 9;
	private static final int MAGIC = 0;
	private static final int UPGRADE_BONUS = 5;

	public HybridMovement() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = MAGIC;
		baseBlock = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new GainBlockAction(p, p, block));
		if (upgraded) {
			addToBot(new ScryAction(magicNumber));
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new HybridMovement();
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
