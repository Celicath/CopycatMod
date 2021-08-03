package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.PCAGainGoldAction;
import TheCopycat.actions.PCAVFXAction;
import TheCopycat.utils.GameLogicUtils;
import basemod.AutoAdd;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

import java.util.ArrayList;

@AutoAdd.Seen
public class DynamicCard extends AbstractMonsterCard {
	private static final String RAW_ID = "DynamicCard";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = -2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.NONE;
	public static String
			blockDesc, damageDesc, multiDesc, drawDesc, buffPrefix, buffSuffix, debuffPrefix, debuffSuffix,
			gainEDesc, enterCalmDesc, exhaustOtherDesc, discardDesc, vampireDesc, goldDesc, strengthDownDesc, exhaustDesc,
			metallicizeDesc, shackleDesc;

	/* parameters */
	public int baseCost;
	public int hits = 0;
	public boolean isDraw = false;
	public boolean isDiscard = false;
	public boolean exhaustOther = false;
	public boolean gainEnergy = false;
	public boolean enterCalm = false;
	public boolean isVampire = false;
	public boolean stealGold = false;
	public String buffs = "";
	public String debuffs = "";

	/* automatic set fields */
	boolean shouldUpgradeCost = false;
	// public boolean exhaust;

	public boolean initialized = false;
	public boolean invalid = false;
	public boolean empty = false;

	public static void initializeDescriptionParts() {
		blockDesc = CardCrawlGame.languagePack.getCardStrings("Defend_B").DESCRIPTION;
		damageDesc = CardCrawlGame.languagePack.getCardStrings("Strike_B").DESCRIPTION;
		multiDesc = CardCrawlGame.languagePack.getCardStrings("Expunger").EXTENDED_DESCRIPTION[0];
		drawDesc = CardCrawlGame.languagePack.getCardStrings("Skim").DESCRIPTION;
		strengthDownDesc = CardCrawlGame.languagePack.getCardStrings("Disarm").DESCRIPTION;
		exhaustDesc = CardCrawlGame.languagePack.getCardStrings("Slimed").DESCRIPTION;
		goldDesc = CardCrawlGame.languagePack.getCardStrings("FameAndFortune").DESCRIPTION;
		shackleDesc = CardCrawlGame.languagePack.getCardStrings("Dark Shackles").DESCRIPTION;
		buffPrefix = EXTENDED_DESCRIPTION[0];
		buffSuffix = EXTENDED_DESCRIPTION[1];
		debuffPrefix = EXTENDED_DESCRIPTION[2];
		debuffSuffix = EXTENDED_DESCRIPTION[3];
		gainEDesc = EXTENDED_DESCRIPTION[4];
		enterCalmDesc = EXTENDED_DESCRIPTION[5];
		exhaustOtherDesc = EXTENDED_DESCRIPTION[6];
		discardDesc = EXTENDED_DESCRIPTION[7];
		vampireDesc = EXTENDED_DESCRIPTION[8];
		metallicizeDesc = EXTENDED_DESCRIPTION[9];
	}

	public DynamicCard() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET);
	}

	public void setType() {
		if (baseDamage > 0) {
			this.type = CardType.ATTACK;
			this.target = CardTarget.ENEMY;
		} else if (baseBlock > 0 || !debuffs.isEmpty() || isDraw || exhaustOther || isDiscard || gainEnergy || enterCalm) {
			this.type = CardType.SKILL;
			if (debuffs.isEmpty()) {
				this.target = CardTarget.SELF;
			} else {
				this.target = CardTarget.ENEMY;
			}
		} else if (!buffs.isEmpty()) {
			this.type = CardType.POWER;
			this.target = CardTarget.SELF;
		} else {
			this.type = CardType.SKILL;
			this.baseCost = this.cost = this.costForTurn = -2;
			this.target = CardTarget.NONE;
			empty = true;
		}
	}

	void setModifiers(String modifiers) {
		for (int i = 0; i < modifiers.length(); i++) {
			switch (modifiers.charAt(i)) {
				case 'C':
					shouldUpgradeCost = true;
					break;
				case 'D':
					isDraw = true;
					break;
				case 'T':
					exhaust = true;
					break;
				case 'O':
					exhaustOther = true;
					break;
				case 'I':
					isDiscard = true;
					break;
				case 'E':
					gainEnergy = true;
					break;
				case 'L':
					enterCalm = true;
					break;
				case 'V':
					isVampire = true;
					break;
				case 'G':
					stealGold = true;
					break;
				case 'U':
					rarity = CardRarity.UNCOMMON;
					break;
				case 'R':
					rarity = CardRarity.RARE;
					break;
			}
		}
	}

	String getModifierString() {
		StringBuilder result = new StringBuilder();
		if (shouldUpgradeCost) result.append('C');
		if (isDraw) result.append('D');
		if (exhaust) result.append('T');
		if (exhaustOther) result.append('O');
		if (isDiscard) result.append('I');
		if (gainEnergy) result.append('E');
		if (enterCalm) result.append('L');
		if (isVampire) result.append('V');
		if (stealGold) result.append('G');
		if (rarity == CardRarity.UNCOMMON) result.append('U');
		if (rarity == CardRarity.RARE) result.append('R');
		return result.toString();
	}

	public void setMagicNumber(int amount) {
		amount = Math.abs(amount);
		if (baseMagicNumber < amount) {
			baseMagicNumber = magicNumber = amount;
		}
	}

	public void calculateCost() {
		int rarityNum = 0;
		if (empty) {
			return;
		}
		float tmp = 0;
		int forceCost = 0;
		for (int i = 0, len = buffs.length(); i < len; i++) {
			switch (buffs.charAt(i)) {
				case 'S':
				case 'D':
					tmp += baseMagicNumber * 5.1f;
					break;
				case 'P':
				case 'T':
					tmp += baseMagicNumber * 3.2f;
					exhaust = true;
					break;
				case 'M':
					tmp += baseMagicNumber * 3.5f;
					exhaust = true;
					break;
				case 'R':
					rarityNum = 2;
					tmp += baseMagicNumber * 12;
					forceCost = baseMagicNumber;
					exhaust = true;
					break;
			}
		}
		for (int i = 0, len = debuffs.length(); i < len; i++) {
			switch (debuffs.charAt(i)) {
				case 'V':
				case 'W':
					tmp += baseMagicNumber * 3.5f;
					break;
				case 'P':
					tmp += baseMagicNumber * 1.6f;
					break;
				case 'S':
					tmp += baseMagicNumber * 4;
					exhaust = true;
					break;
				case 'E':
					tmp += baseMagicNumber * 0.99f;
					exhaust = true;
					break;
			}
		}

		tmp += Math.max(baseBlock * 1.18f, 0) + (Math.max(baseDamage, 0) * Math.max(hits, 1) + (hits > 1 ? hits - 1 : 0));
		if (hits >= 5) exhaust = true;
		tmp += (isDraw ? baseMagicNumber * 3 : 0) + (exhaustOther ? 2 : 0) - (isDiscard ? 2 : 0) + (enterCalm ? 3 : 0);
		if (isVampire) {
			tmp += baseDamage;
			forceCost = 2;
			exhaust = true;
			rarityNum++;
		}
		if (stealGold) {
			if (baseDamage >= 12) {
				forceCost = 2;
			}
			exhaust = true;
			rarityNum++;
		}
		if (isDraw && baseDamage <= 0 && baseBlock <= 0 && buffs.isEmpty() && debuffs.isEmpty() && !gainEnergy && !enterCalm && !stealGold) {
			if (baseMagicNumber <= 2) {
				baseMagicNumber = magicNumber = 2;
				baseCost = 0;
				exhaust = true;
			} else if (baseMagicNumber <= 4) {
				baseCost = 1;
			} else {
				baseCost = 1;
				exhaust = true;
				shouldUpgradeCost = true;
				rarityNum++;
			}
		} else {
			switch (AbstractDungeon.actNum) {
				case 1:
					if (tmp < 4) {
						baseCost = 0;
						gainEnergy = true;
					} else if (tmp < 7) {
						baseCost = 0;
					} else if (tmp < 8) {
						baseCost = 0;
						rarityNum++;
					} else if (tmp < 10) {
						baseCost = 0;
						if (exhaust) {
							rarityNum++;
						} else {
							exhaust = true;
						}
					} else if (tmp < 12) {
						baseCost = 1;
					} else if (tmp < 13) {
						baseCost = 1;
						rarityNum++;
					} else if (tmp < 16) {
						baseCost = 1;
						if (exhaust) {
							rarityNum++;
						} else {
							exhaust = true;
						}
					} else if (tmp < 20) {
						baseCost = 2;
					} else if (tmp < 25) {
						baseCost = 2;
						rarityNum++;
					} else if (tmp < 31) {
						baseCost = 2;
						exhaust = true;
					} else if (tmp < 35) {
						baseCost = 3;
					} else if (tmp < 38) {
						baseCost = 3;
						rarityNum++;
					} else if (tmp < 41) {
						baseCost = 3;
						rarityNum = 2;
					} else {
						baseCost = 3;
						exhaust = true;
						rarityNum++;
					}
					break;
				case 2:
					if (tmp < 4) {
						baseCost = 0;
						gainEnergy = true;
					} else if (tmp < 7) {
						baseCost = 0;
					} else if (tmp < 9) {
						baseCost = 0;
						rarityNum++;
					} else if (tmp < 11) {
						baseCost = 0;
						if (exhaust) {
							rarityNum++;
						} else {
							exhaust = true;
						}
					} else if (tmp < 12) {
						baseCost = 1;
					} else if (tmp < 14) {
						baseCost = 1;
						rarityNum++;
					} else if (tmp < 17) {
						baseCost = 1;
						if (exhaust) {
							rarityNum++;
						} else {
							exhaust = true;
						}
					} else if (tmp < 20) {
						baseCost = 2;
					} else if (tmp < 26) {
						baseCost = 2;
						rarityNum++;
					} else if (tmp < 32) {
						baseCost = 2;
						if (exhaust) {
							rarityNum++;
						} else {
							exhaust = true;
						}
					} else if (tmp < 35) {
						baseCost = 3;
					} else if (tmp < 39) {
						baseCost = 3;
						rarityNum++;
					} else if (tmp < 43) {
						baseCost = 3;
						rarityNum = 2;
					} else {
						baseCost = 3;
						exhaust = true;
						rarityNum++;
					}
					break;
				default:
					if (tmp <= 4) {
						baseCost = 0;
						gainEnergy = true;
					} else if (tmp <= 7) {
						baseCost = 0;
					} else if (tmp <= 10) {
						baseCost = 0;
						rarityNum++;
					} else if (tmp <= 12) {
						baseCost = 1;
					} else if (tmp <= 15) {
						baseCost = 1;
						rarityNum++;
					} else if (tmp <= 17.5f) {
						baseCost = 1;
						if (exhaust) {
							rarityNum++;
						} else {
							exhaust = true;
						}
					} else if (tmp <= 20) {
						baseCost = 2;
					} else if (tmp <= 25) {
						baseCost = 2;
						rarityNum++;
					} else if (tmp <= 30) {
						baseCost = 2;
						rarityNum = 2;
					} else if (tmp <= 35) {
						baseCost = 3;
					} else if (tmp <= 40) {
						baseCost = 3;
						rarityNum++;
					} else if (tmp <= 45) {
						baseCost = 3;
						rarityNum = 2;
					} else {
						baseCost = 3;
						exhaust = true;
						rarityNum++;
					}
			}
		}
		if (exhaust && type == CardType.POWER) {
			exhaust = false;
			baseCost++;
			shouldUpgradeCost = true;
		}
		if (forceCost > baseCost) {
			baseCost = forceCost;
		}
		cost = costForTurn = baseCost;

		if (rarityNum == 0) {
			rarity = CardRarity.COMMON;
		} else if (rarityNum == 1) {
			rarity = CardRarity.UNCOMMON;
		} else {
			rarity = CardRarity.RARE;
		}
	}

	public void setCard(String name, int cost, int block, int damage, int hits, int magicNumber, String modifiers, String buffs, String debuffs) {
		this.originalName = this.name = name;
		if (upgraded) {
			this.name = this.name + "+";
		}
		this.baseCost = this.cost = this.costForTurn = cost;
		this.buffs = buffs;
		this.debuffs = debuffs;
		this.baseBlock = this.block = block;
		this.baseDamage = this.damage = damage;
		this.hits = hits;
		this.baseMagicNumber = this.magicNumber = magicNumber;
		setModifiers(modifiers);
		setType();
		updateDescription();
	}

	public static AbstractPower getBuffPower(AbstractCreature c, char powChar, int amount) {
		switch (powChar) {
			case 'S':
				return new StrengthPower(c, amount);
			case 'D':
				return new DexterityPower(c, amount);
			case 'P':
				return new PlatedArmorPower(c, amount);
			case 'T':
				return new ThornsPower(c, amount);
			case 'R':
				return new RitualPower(c, amount, true);
			case 'M':
				return new MetallicizePower(c, amount);
			default:
				return null;
		}
	}

	public static AbstractPower getDebuffPower(AbstractCreature target, AbstractCreature source, char powChar, int amount) {
		switch (powChar) {
			case 'P':
				return new PoisonPower(target, source, amount);
			case 'V':
				return new VulnerablePower(target, amount, false);
			case 'W':
				return new WeakPower(target, amount, false);
			case 'S':
				return new StrengthPower(target, -amount);
			case 'E':  // should handle manually
			default:
				return null;
		}
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (!initialized || invalid || empty) {
			return;
		}
		if (baseBlock > 0) {
			addToBot(new GainBlockAction(p, p, block));
		}
		if (baseDamage > 0) {
			AbstractGameAction.AttackEffect effect;
			switch (baseCost) {
				case 0:
				case 1:
					effect = AbstractGameAction.AttackEffect.SLASH_DIAGONAL;
					break;
				case 2:
					effect = AbstractGameAction.AttackEffect.BLUNT_LIGHT;
					break;
				default:
					effect = AbstractGameAction.AttackEffect.BLUNT_HEAVY;
					break;
			}
			if (isVampire) {
				effect = AbstractGameAction.AttackEffect.NONE;
				addToBot(new VFXAction(new BiteEffect(m.hb.cX, m.hb.cY, Color.GOLD.cpy()), 0.0F));
			} else if (baseCost >= 3 && hits <= 1) {
				addToBot(new VFXAction(new WeightyImpactEffect(m.hb.cX, m.hb.cY)));
			}
			for (int i = 0; i < Math.max(hits, 1); i++) {
				if (isVampire) {
					addToBot(new VampireDamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), effect));
				} else {
					addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), effect));
				}
			}
		}
		if (stealGold) {
			addToBot(new PCAGainGoldAction(magicNumber));
			if (m != null) {
				for (int i = 0; i < magicNumber; i++) {
					addToBot(new PCAVFXAction(new GainPennyEffect(p, m.hb.cX, m.hb.cY, p.hb.cX, p.hb.cY, true), 0.0F));
				}
			} else {
				addToBot(new PCAVFXAction(new RainingGoldEffect(magicNumber * 2, true), 0.0F));
			}
		}
		if (isDraw) {
			addToBot(new DrawCardAction(p, magicNumber));
		}
		for (int i = 0, len = buffs.length(); i < len; i++) {
			AbstractPower pow = getBuffPower(p, buffs.charAt(i), magicNumber);
			if (pow != null) {
				addToBot(new ApplyPowerAction(p, p, pow));
			}
		}

		if (gainEnergy) {
			addToBot(new GainEnergyAction(1));
		}

		if (enterCalm) {
			addToBot(new ChangeStanceAction(new CalmStance()));
		}

		for (int i = 0, len = debuffs.length(); i < len; i++) {
			AbstractPower pow = getDebuffPower(m, p, debuffs.charAt(i), magicNumber);
			if (pow != null) {
				addToBot(new ApplyPowerAction(m, p, pow));
			}
			if (debuffs.charAt(i) == 'E') {
				addToBot(new ApplyPowerAction(m, p, new StrengthPower(m, -magicNumber), -magicNumber));
				if (m != null && !m.hasPower(ArtifactPower.POWER_ID)) {
					addToBot(new ApplyPowerAction(m, p, new GainStrengthPower(m, magicNumber), magicNumber));
				}
			}
		}

		if (exhaustOther) {
			addToBot(new ExhaustAction(1, false));
		}
		if (isDiscard) {
			addToBot(new DiscardAction(p, p, 1, false));
		}
	}

	String buffDesc(String name, boolean capitalize) {
		if (capitalize) {
			return buffPrefix + TipHelper.capitalize(name) + buffSuffix;
		} else {
			return buffPrefix + name + buffSuffix;
		}
	}

	String debuffDesc(String name) {
		return debuffPrefix + TipHelper.capitalize(name) + debuffSuffix;
	}

	public void updateDescription() {
		ArrayList<String> desc = new ArrayList<>();
		if (baseBlock > 0) {
			desc.add(blockDesc);
		}
		if (baseDamage > 0) {
			String result = hits > 1 ? multiDesc.replace("!M!", String.valueOf(hits)) : damageDesc;
			if (isVampire) {
				result += " " + vampireDesc;
			}
			desc.add(result);
		}
		if (isDraw) {
			desc.add(drawDesc);
		}

		for (int i = 0, len = buffs.length(); i < len; i++) {
			switch (buffs.charAt(i)) {
				case 'S':
					desc.add(buffDesc(GameDictionary.STRENGTH.NAMES[0], true));
					break;
				case 'D':
					desc.add(buffDesc(GameDictionary.DEXTERITY.NAMES[0], true));
					break;
				case 'P':
					desc.add(buffDesc(PlatedArmorPower.NAME, false));
					break;
				case 'T':
					desc.add(buffDesc(GameDictionary.THORNS.NAMES[0], true));
					break;
				case 'R':
					desc.add(buffDesc(GameDictionary.RITUAL.NAMES[0], true));
					break;
				case 'M':
					desc.add(buffDesc(MetallicizePower.NAME, false));
					break;
			}
		}

		boolean disarm = false;
		boolean shackles = false;
		for (int i = 0, len = debuffs.length(); i < len; i++) {
			switch (debuffs.charAt(i)) {
				case 'P':
					desc.add(debuffDesc(GameDictionary.POISON.NAMES[0]));
					break;
				case 'V':
					desc.add(debuffDesc(GameDictionary.VULNERABLE.NAMES[0]));
					break;
				case 'W':
					desc.add(debuffDesc(GameDictionary.WEAK.NAMES[0]));
					break;
				case 'E':
					shackles = true;
					break;
				case 'S':
					disarm = true;
					break;
			}
		}

		if (gainEnergy) {
			desc.add(gainEDesc);
		}
		if (enterCalm) {
			desc.add(enterCalmDesc);
		}
		if (exhaustOther) {
			desc.add(exhaustOtherDesc);
		}
		if (isDiscard) {
			desc.add(discardDesc);
		}
		if (stealGold) {
			desc.add(goldDesc);
		}
		if (disarm) {
			desc.add(strengthDownDesc);
		} else if (shackles) {
			desc.add(shackleDesc);
		} else if (exhaust) {
			desc.add(exhaustDesc);
		}
		rawDescription = String.join(" NL ", desc);
		initializeDescription();
		initialized = true;
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			if (shouldUpgradeCost) {
				upgradeBaseCost(baseCost - 1);
			} else {
				int level = baseCost + (exhaust ? 3 : 2);
				if (level < 3) level = 3;
				if (level >= 6) level = 20;
				else {
					if (baseDamage > 0 && baseBlock > 0) level--;
					if (magicNumber > 0) level--;
					if (level == 5) level++;
				}
				if (baseDamage > 0) {
					int up = level;
					if (level > 10) {
						up = baseDamage * level / 100;
					} else if (hits > 1) {
						up = (up + hits - 1) / hits;
					}
					if (isVampire) {
						up = (up + 1) / 2;
					}
					if (up < 1) up = 1;
					upgradeDamage(up);
				}
				if (baseBlock > 0) {
					upgradeBlock(level > 10 ? baseBlock * level / 100 : level);
				}
				if (baseMagicNumber > 0) {
					if (debuffs.equals("P")) {
						upgradeMagicNumber(Math.max(baseCost + (exhaust ? 2 : 1), 2));
					} else {
						upgradeMagicNumber(baseMagicNumber >= 4 ? 2 : 1);
					}
				}
			}
		}
	}

	public void calculateMonsterCardID() {
		ArrayList<String> result = new ArrayList<>();
		result.add(ID);
		result.add(originalName);
		result.add(String.valueOf(baseCost));
		result.add(String.valueOf(baseBlock));
		result.add(String.valueOf(baseDamage));
		result.add(String.valueOf(hits));
		result.add(String.valueOf(baseMagicNumber));
		result.add(getModifierString());
		result.add(buffs);
		result.add(debuffs);

		monsterCardID = String.join(GameLogicUtils.metricIdSeparator, result);
	}

	@Override
	public void loadFromTokens(String[] tokens) {
		if (tokens.length < 10) {
			invalid = true;
		} else {
			try {
				int shouldUpgrade = 0;
				if (upgraded) {
					upgraded = false;
					shouldUpgrade = timesUpgraded > 0 ? timesUpgraded : 1;
				}
				setCard(tokens[1],
						Integer.parseInt(tokens[2]),
						Integer.parseInt(tokens[3]),
						Integer.parseInt(tokens[4]),
						Integer.parseInt(tokens[5]),
						Integer.parseInt(tokens[6]),
						tokens[7],
						tokens[8],
						tokens[9]);
				for (int i = 0; i < shouldUpgrade; i++) {
					upgrade();
				}
			} catch (Exception e) {
				e.printStackTrace();
				invalid = true;
			}
		}
	}

	public void addBuff(char c) {
		if (buffs.indexOf(c) == -1) {
			buffs += c;
		}
	}

	public boolean addDebuff(char c) {
		if (debuffs.indexOf(c) == -1) {
			debuffs += c;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AbstractCard makeCopy() {
		DynamicCard c = new DynamicCard();
		if (initialized) {
			c.loadFromMonsterCardID(monsterCardID);
			c.loadTexture(monsterModelID);
		}
		return c;
	}
}
