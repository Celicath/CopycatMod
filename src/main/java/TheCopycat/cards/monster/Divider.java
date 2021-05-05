package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.HoverMonsterCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Hexaghost;
import com.megacrit.cardcrawl.vfx.combat.GhostIgniteEffect;

public class Divider extends AbstractMonsterCard implements HoverMonsterCard {
	private static final String RAW_ID = "Divider";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = 3;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int DAMAGE_BONUS = 1;
	private static final int UPGRADE_BONUS = 1;
	private static final int HITS = 6;
	private static final int DIV = 12;

	public Divider() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Hexaghost.ID, 1);
		baseMagicNumber = magicNumber = DAMAGE_BONUS;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		calculateCardDamage(m);
		for (int i = 0; i < HITS; i++) {
			addToBot(new VFXAction(p, new GhostIgniteEffect(m.hb.cX + MathUtils.random(-80.0F, 80.0F) * Settings.scale, m.hb.cY + MathUtils.random(-80.0F, 80.0F) * Settings.scale), 0.05F));
			if (MathUtils.randomBoolean()) {
				addToBot(new SFXAction("GHOST_ORB_IGNITE_1", 0.3F));
			} else {
				addToBot(new SFXAction("GHOST_ORB_IGNITE_2", 0.3F));
			}

			addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
		}
		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				rawDescription = DESCRIPTION;
				initializeDescription();
				isDone = true;
			}
		});
	}

	@Override
	public void calculateCardDamage(AbstractMonster m) {
		if (m != null) {
			baseDamage = m.currentHealth / DIV + magicNumber;
		} else {
			baseDamage = -1;
		}
		super.calculateCardDamage(m);
	}

	@Override
	public AbstractCard makeCopy() {
		return new Divider();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}

	@Override
	public void onHoverMonster(AbstractMonster m) {
		rawDescription = EXTENDED_DESCRIPTION[0];
		initializeDescription();
	}

	@Override
	public void onUnhoverMonster() {
		rawDescription = DESCRIPTION;
		initializeDescription();
	}
}
