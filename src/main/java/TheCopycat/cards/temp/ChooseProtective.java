package TheCopycat.cards.temp;

import TheCopycat.CopycatModMain;
import TheCopycat.stances.ProtectiveStance;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChooseProtective extends CustomCard {
	public static final String RAW_ID = "Protective";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = -2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.STATUS;
	private static final CardColor COLOR = CardColor.COLORLESS;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.NONE;

	public ChooseProtective() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
	}

	public void onChoseThisOption() {
		addToBot(new ChangeStanceAction(new ProtectiveStance()));
	}

	public void upgrade() {
	}

	public AbstractCard makeCopy() {
		return new ChooseProtective();
	}
}
