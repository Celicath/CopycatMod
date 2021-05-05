package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.MeFirstAction;
import TheCopycat.interfaces.HoverMonsterCard;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.enums.MonsterIntentEnum;

import static com.megacrit.cardcrawl.monsters.AbstractMonster.Intent.*;

public class MeFirst extends CustomCard implements HoverMonsterCard {
	private static final String RAW_ID = "MeFirst";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int DAMAGE = 9;
	private static final int UPGRADE_DAMAGE = 3;
	private static final int BLOCK = 9;
	private static final int UPGRADE_BLOCK = 3;
	private static final int MAGIC_POWER = 3;
	private static final int UPGRADE_MAGIC = 1;

	public MeFirst() {
		super(ID, NAME, IMG, COST, getRawDescription(null), TYPE, COLOR, RARITY, TARGET);
		baseDamage = DAMAGE;
		baseBlock = BLOCK;
		baseMagicNumber = magicNumber = MAGIC_POWER;
	}

	public static boolean checkActivate(AbstractMonster m, int mode) {
		if (m == null) {
			return true;
		}
		switch (mode) {
			case 0:
				return m.intent == ATTACK_DEFEND || m.intent == DEFEND || m.intent == DEFEND_DEBUFF || m.intent == DEFEND_BUFF || m.intent == MonsterIntentEnum.ATTACK_MINION_DEFEND;
			case 1:
				return m.intent == ATTACK || m.intent == ATTACK_BUFF || m.intent == ATTACK_DEBUFF || m.intent == ATTACK_DEFEND || m.getIntentBaseDmg() >= 0 ||
						m.intent == MonsterIntentEnum.ATTACK_MINION ||
						m.intent == MonsterIntentEnum.ATTACK_MINION_BUFF ||
						m.intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF ||
						m.intent == MonsterIntentEnum.ATTACK_MINION_DEFEND;
			case 2:
				return m.intent == ATTACK_BUFF || m.intent == BUFF || m.intent == DEFEND_BUFF || m.intent == MAGIC || m.intent == MonsterIntentEnum.ATTACK_MINION_BUFF;
			case 3:
				return m.intent == ATTACK_DEBUFF || m.intent == DEBUFF || m.intent == STRONG_DEBUFF || m.intent == DEFEND_DEBUFF || m.intent == MAGIC || m.intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF;
			default:
				return true;
		}
	}

	public static String getRawDescription(AbstractMonster m) {
		StringBuilder result = new StringBuilder(DESCRIPTION);
		for (int i = 0; i < cardStrings.EXTENDED_DESCRIPTION.length; i++) {
			result.append(" NL");
			if (checkActivate(m, i)) {
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
		}
		return result.toString();
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
