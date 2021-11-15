package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.monster.AbstractMonsterCard;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ThrowDaggerEffect;

public class MonsterStudy extends CustomCard {
	private static final String RAW_ID = "MonsterStudy";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 8;
	private static final int UPGRADE_BONUS = 2;
	private static final int BLOCK = 2;
	private static final int UPGRADE_BLOCK = 1;

	public MonsterStudy() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseDamage = POWER;
		baseBlock = BLOCK;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (m != null) {
			addToBot(new VFXAction(new ThrowDaggerEffect(m.hb.cX, m.hb.cY)));
		}
		addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				for (AbstractCard c : AbstractDungeon.player.hand.group) {
					if (c instanceof AbstractMonsterCard) {
						addToTop(new GainBlockAction(p, block, true));
					}
				}
				isDone = true;
			}
		});
	}

	@Override
	public void applyPowers() {
		super.applyPowers();
		int count = (int) AbstractDungeon.player.hand.group.stream().filter(c -> c instanceof AbstractMonsterCard).count();

		rawDescription = cardStrings.DESCRIPTION;
		rawDescription += cardStrings.EXTENDED_DESCRIPTION[0] + count;
		if (count == 1) {
			rawDescription += cardStrings.EXTENDED_DESCRIPTION[1];
		} else {
			rawDescription += cardStrings.EXTENDED_DESCRIPTION[2];
		}

		initializeDescription();
	}

	public void onMoveToDiscard() {
		rawDescription = cardStrings.DESCRIPTION;
		initializeDescription();
	}

	@Override
	public AbstractCard makeCopy() {
		return new MonsterStudy();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
			upgradeBlock(UPGRADE_BLOCK);
		}
	}
}
