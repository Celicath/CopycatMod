package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SummonCopycatMinionAction;
import TheCopycat.friendlyminions.MirrorMinion;
import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.SnakeDagger;
import com.megacrit.cardcrawl.monsters.city.BronzeOrb;
import com.megacrit.cardcrawl.monsters.city.TorchHead;
import com.megacrit.cardcrawl.monsters.exordium.*;

import java.util.ArrayList;

@AutoAdd.Seen
public class SummonMonsterCard extends AbstractMonsterCard {
	private static final String RAW_ID = "SummonMonsterCard";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.SELF;

	public String summonID = null;
	public String summonName = null;
	public int baseCost;
	public boolean initialized = false;
	public boolean smallFont = false;

	public static AbstractMonster makeMonster(String ID) {
		switch (ID) {
			case AcidSlime_M.ID:
				return new AcidSlime_M(0.0f, 0.0f);
			case AcidSlime_L.ID:
				return new AcidSlime_L(0.0f, 0.0f);
			case SpikeSlime_M.ID:
				return new SpikeSlime_M(0.0f, 0.0f);
			case SpikeSlime_L.ID:
				return new SpikeSlime_L(0.0f, 0.0f);
			case GremlinWarrior.ID:
				return new GremlinWarrior(0.0f, 0.0f);
			case GremlinFat.ID:
				return new GremlinFat(0.0f, 0.0f);
			case GremlinThief.ID:
				return new GremlinThief(0.0f, 0.0f);
			case GremlinTsundere.ID:
				return new GremlinTsundere(0.0f, 0.0f);
			case GremlinWizard.ID:
				return new GremlinWizard(0.0f, 0.0f);
			case SnakeDagger.ID:
				return new SnakeDagger(0.0f, 0.0f);
			case BronzeOrb.ID:
				return new BronzeOrb(0.0f, 0.0f, 0);
			case TorchHead.ID:
				return new TorchHead(0.0f, 0.0f);
			default:
				return null;
		}
	}

	public SummonMonsterCard() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET);
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractMonster summon = makeMonster(summonID);
		if (summon == null) {
			addToBot(new TalkAction(true, "This minion is not supported.", 0.5f, 4.0f));
		} else {
			addToBot(new SummonCopycatMinionAction(new MirrorMinion(summon.name, summon, magicNumber)));
		}
	}

	public void calculateMonsterCardID() {
		ArrayList<String> result = new ArrayList<>();
		result.add(ID);
		result.add(originalName);
		result.add(String.valueOf(summonID));
		result.add(String.valueOf(summonName));
		result.add(String.valueOf(baseCost));
		result.add(String.valueOf(baseMagicNumber));
		result.add(String.valueOf(smallFont));

		monsterCardID = String.join(idSeparator, result);
	}

	public void updateDescription() {
		rawDescription = EXTENDED_DESCRIPTION[1] + summonName + EXTENDED_DESCRIPTION[2];
		initializeDescription();
		initialized = true;
	}

	public void loadFromMonsterCardID(String id) {
		monsterCardID = id;
		loadFromTokens(id.split(idSeparator, -1));
	}

	@Override
	public void loadFromTokens(String[] tokens) {
		if (tokens.length >= 7) {
			try {
				int shouldUpgrade = 0;
				if (upgraded) {
					upgraded = false;
					shouldUpgrade = timesUpgraded > 0 ? timesUpgraded : 1;
				}
				originalName = name = tokens[1];
				summonID = tokens[2];
				summonName = tokens[3];
				baseCost = cost = costForTurn = Integer.parseInt(tokens[4]);
				baseMagicNumber = magicNumber = Integer.parseInt(tokens[5]);
				smallFont = tokens[6].equals("true");
				updateDescription();
				for (int i = 0; i < shouldUpgrade; i++) {
					upgrade();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onLoad(String id) {
		loadFromTokens(id.split(idSeparator, -1));
		monsterCardID = id;

		super.onLoad(id);
	}

	public void setName(String cardName, AbstractMonster m) {
		if (cardName == null) {
			originalName = name = EXTENDED_DESCRIPTION[0] + m.name;
			smallFont = true;
		} else {
			originalName = name = cardName;
			smallFont = false;
		}
	}

	public void setSummon(AbstractMonster m, int cost, int hp) {
		summonID = m.id;
		summonName = m.name;
		baseCost = cost = costForTurn = cost;
		baseMagicNumber = magicNumber = hp;
		updateDescription();
	}

	@Override
	public AbstractCard makeCopy() {
		SummonMonsterCard c = new SummonMonsterCard();
		if (initialized) {
			c.loadFromMonsterCardID(monsterCardID);
			c.loadTexture(monsterModelID);
		}
		return c;
	}

	@Override
	public float getTitleFontSize() {
		if (smallFont) {
			return 20;
		} else {
			return super.getTitleFontSize();
		}
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(4);
		}
	}
}
