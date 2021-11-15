package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.MeFirstAction;
import TheCopycat.interfaces.HoverMonsterCard;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.utils.GameLogicUtils;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MeFirst extends CustomCard implements HoverMonsterCard {
	private static final String RAW_ID = "MeFirst";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int DAMAGE = 10;
	private static final int UPGRADE_DAMAGE = 4;
	private static final int BLOCK = 10;
	private static final int UPGRADE_BLOCK = 4;
	private static final int MAGIC_POWER = 3;
	private static final int UPGRADE_MAGIC = 1;

	public MeFirst() {
		super(ID, NAME, IMG, COST, getRawDescription(null), TYPE, COLOR, RARITY, TARGET);
		baseDamage = DAMAGE;
		baseBlock = BLOCK;
		baseMagicNumber = magicNumber = MAGIC_POWER;
	}

	public static String getRawDescription(AbstractMonster m) {
		StringBuilder result = new StringBuilder(DESCRIPTION);
		for (int i = 0; i < cardStrings.EXTENDED_DESCRIPTION.length; i++) {
			if (GameLogicUtils.checkIntent(m, i)) {
				result.append(" ").append(cardStrings.EXTENDED_DESCRIPTION[i]);
			} else {
				for (String word : cardStrings.EXTENDED_DESCRIPTION[i].split(" ")) {
					if (word.startsWith("!")) {
						result.append(" ").append(word);
					} else {
						result.append(" [#a0a0a0]").append(word).append("[]");
					}
				}
			}
			result.append(" NL");
		}
		result.append(" \u00A0");
		return result.toString();
	}

	@Override
	public void onMoveToDiscard() {
		onUnhoverMonster();
	}

	@Override
	public void onHoverMonster(AbstractMonster m) {
		rawDescription = getRawDescription(m);
		initializeDescription();
	}

	@Override
	public void onUnhoverMonster() {
		rawDescription = getRawDescription(null);
		initializeDescription();
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new MeFirstAction(m, new DamageInfo(p, damage, damageTypeForTurn), block, magicNumber));
	}

	@Override
	public AbstractCard makeCopy() {
		return new MeFirst();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_DAMAGE);
			upgradeBlock(UPGRADE_BLOCK);
			upgradeMagicNumber(UPGRADE_MAGIC);
		}
	}
}
