package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.RedirectMinionAction;
import TheCopycat.interfaces.TargetAllyCard;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.patches.TargetAllyPatch;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class Spotlight extends CustomCard implements TargetAllyCard {
	private static final String RAW_ID = "Spotlight";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 7;
	private static final int UPGRADE_BONUS = 3;

	private AbstractCreature hovering = null;

	public Spotlight() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractCreature targetAlly = TargetAllyPatch.cardTargetMap.get(uuid);
		if (targetAlly == null) {
			targetAlly = AbstractDungeon.player;
		}

		addToBot(new GainBlockAction(targetAlly, magicNumber));
		addToBot(new RedirectMinionAction(targetAlly));
	}

	@Override
	public AbstractCard makeCopy() {
		return new Spotlight();
	}

	@Override
	public void calculateCardDamage(AbstractMonster mo) {
		ArrayList<AbstractPower> temp = AbstractDungeon.player.powers;
		hovering = TargetAllyPatch.hoveredally;
		if (hovering != null) {
			if (TargetAllyPatch.hoveredally != AbstractDungeon.player) {
				AbstractDungeon.player.powers = hovering.powers;
			}
			baseBlock = baseMagicNumber;
			super.calculateCardDamage(mo);
			magicNumber = block;
			isMagicNumberModified = isBlockModified;
			AbstractDungeon.player.powers = temp;
		} else {
			super.calculateCardDamage(mo);
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		if (hovering != null && TargetAllyPatch.hoveredally == null) {
			hovering = null;
			magicNumber = baseMagicNumber;
			isMagicNumberModified = false;
		}
		super.render(sb);
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
