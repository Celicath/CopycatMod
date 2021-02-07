package TheCopycat.patches;

import TheCopycat.cards.monster.*;
import TheCopycat.utils.Shader;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.spine.Skeleton;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.GainBlockRandomMonsterAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.city.*;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.GhostlyFireEffect;
import com.megacrit.cardcrawl.vfx.GhostlyWeakFireEffect;
import javassist.CtBehavior;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.HashMap;

public class CaptureEnemyMovePatch {
	public static HashMap<String, AbstractMonsterCard> generatedCards = new HashMap<>();
	public static HashMap<AbstractMonster, AbstractMonsterCard> lastMoveCards = new HashMap<>();
	static AbstractMonster curMonster = null;
	static AbstractMonsterCard curCard = null;
	static int curMove = -1;
	static String id;
	static boolean frail = false;
	static boolean suicide = false;
	static boolean hasUniqueName = false;

	// render
	static OrthographicCamera camera;
	static SpriteBatch sb = new SpriteBatch();
	public static int WIDTH = 500;
	public static int HEIGHT = 380;
	public static float CAMERA_WIDTH = 350 * Settings.scale;
	public static float CAMERA_HEIGHT = 266 * Settings.scale;

	static FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, WIDTH, HEIGHT, false);

	static {
		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.position.set(WIDTH / 2.0F, HEIGHT / 2.0F, 0.0F);
		camera.update();
		sb.setProjectionMatrix(camera.combined);
		camera.viewportWidth = CAMERA_WIDTH;
		camera.viewportHeight = CAMERA_HEIGHT;
	}

	public static HashMap<String, String> powerCodeMap = new HashMap<String, String>() {
		{
			put(StrengthPower.POWER_ID, "S");
			put(DexterityPower.POWER_ID, "D");
			put(PlatedArmorPower.POWER_ID, "P");
			put(ThornsPower.POWER_ID, "T");
			put(SharpHidePower.POWER_ID, "T");
			put(RitualPower.POWER_ID, "R");
			put(FrailPower.POWER_ID, "F");
			put(VulnerablePower.POWER_ID, "V");
			put(WeakPower.POWER_ID, "W");
			put(PoisonPower.POWER_ID, "P");
			put(ConstrictedPower.POWER_ID, "P");
			put(EntanglePower.POWER_ID, "E");
			put(MetallicizePower.POWER_ID, "M");
			put(DrawReductionPower.POWER_ID, "H");
		}
	};

	@SpirePatch(clz = GameActionManager.class, method = "getNextAction")
	public static class TakeTurnCapture {

		@SpireInsertPatch(locator = BeforeTakeTurnLocator.class)
		public static void Insert(GameActionManager __instance, AbstractMonster ___m) {
			curMonster = ___m;
			EnemyMoveInfo move = ReflectionHacks.getPrivate(___m, AbstractMonster.class, "move");
			curMove = move.nextMove;
			id = ___m.id + "_" + curMove;
			suicide = false;

			/* Special enemy moves */
			if (___m instanceof GremlinWizard && curMove == 1) {
				curCard = new UltimateBlast();
			} else if (___m instanceof GremlinWizard && curMove == 2) {
				curCard = new Charging();
			} else if (___m instanceof GremlinNob && curMove == 3) {
				curCard = new Enrage();
			} else if (___m instanceof Byrd && curMove == 2) {
				curCard = new Fly();
			} else if (___m instanceof AcidSlime_L && curMove == 2) {
				curCard = new Fly();
			} else if (___m instanceof Healer && curMove == 2) {
				curCard = new AreaHeal();
			} else if ((___m instanceof Looter || ___m instanceof Mugger) && curMove == 3) {
				curCard = new Escape();
			} else if (___m instanceof Darkling && curMove == 5 || ___m instanceof AwakenedOne && curMove == 3) {
				curCard = new Reincarnate();
			} else if (___m instanceof Chosen && curMove == 4) {
				curCard = new Hex();
			} else if (___m instanceof Snecko && curMove == 1) {
				curCard = new PerplexingEye();
			} else if (___m instanceof Maw && curMove == 5) {
				curCard = new Nom();
			} else if (___m instanceof WrithingMass && curMove == 4) {
				curCard = new Implant();
			} else if (___m instanceof Transient && curMove == 1) {
				curCard = new FadingAttack();
			} else if (___m instanceof BookOfStabbing && curMove == 1) {
				if (AbstractDungeon.ascensionLevel < 18) {
					curCard = new MultiStabbing();
				} else {
					curCard = new MultiStabbing2();
				}
			} else if (___m instanceof GiantHead && curMove == 2) {
				curCard = new ItIsTime();
			} else if (___m instanceof Hexaghost && curMove == 1) {
				curCard = new Divider();
			} else if (___m instanceof Hexaghost && curMove == 6) {
				curCard = new Inferno();
			} else if (___m instanceof Champ && curMove == 7) {
				curCard = new ChampsAnger();
			} else if (___m instanceof TimeEater && curMove == 5) {
				if (AbstractDungeon.ascensionLevel < 19) {
					curCard = new Haste();
				} else {
					curCard = new Haste2();
				}
			} else if (___m instanceof CorruptHeart && curMove == 3) {
				curCard = new Debilitate();
			} else {
				/* DynamicCard */
				DynamicCard genCard = new DynamicCard();
				frail = false;

				if (___m.moveName != null) {
					hasUniqueName = true;
					genCard.originalName = ___m.moveName;
				} else {
					hasUniqueName = false;
					genCard.originalName = ___m.name + DynamicCard.EXTENDED_DESCRIPTION[DynamicCard.EXTENDED_DESCRIPTION.length - 1] + curMove;
				}
				genCard.name = genCard.originalName;

				genCard.baseDamage = move.baseDamage;
				if (move.isMultiDamage) {
					genCard.hits = move.multiplier;
				}
				if (move.baseDamage > 0 && ___m.hasPower(ThieveryPower.POWER_ID)) {
					genCard.stealGold = true;
				}
				curCard = genCard;
			}
		}

		@SpirePostfixPatch
		public static void Postfix(GameActionManager __instance) {
			if (curCard == null) return;
			do {
				if (curCard instanceof DynamicCard) {
					DynamicCard genCard = (DynamicCard) curCard;
					if (frail) {
						if (!genCard.addDebuff('V')) {
							genCard.addDebuff('P');
						}
					}
					if (genCard.debuffs.indexOf('E') != -1) {
						if (!genCard.isDraw && !genCard.stealGold && genCard.buffs.isEmpty() && genCard.debuffs.equals("E")) {
							genCard.setMagicNumber(10);
						} else if (genCard.debuffs.indexOf('S') != -1) {
							genCard.debuffs = genCard.debuffs.replace("E", "");
						}
					}
					genCard.setType();
					genCard.calculateCost();
					if (curMonster instanceof Champ && curMove == 2) {
						if (AbstractDungeon.ascensionLevel < 9) {
							genCard.baseCost = genCard.cost = 2;
						} else if (AbstractDungeon.ascensionLevel < 19) {
							genCard.baseCost = genCard.cost = 3;
						} else {
							genCard.baseCost = genCard.cost = 4;
						}
					}
					if (genCard.stealGold && genCard.baseMagicNumber <= 0) {
						genCard.setMagicNumber(2 + genCard.baseCost * 3);
					}
					genCard.calculateMonsterCardID();
					if (genCard.empty) {
						break;
					}
					genCard.updateDescription();
				} else if (curCard instanceof SummonCard) {
					((SummonCard) curCard).calculateMonsterCardID();
				} else {
					if (!UnlockTracker.isCardSeen(curCard.cardID)) {
						UnlockTracker.markCardAsSeen(curCard.cardID);
					}
				}
				generatedCards.put(id, curCard);
				lastMoveCards.put(curMonster, curCard);

				if (!curCard.loadTexture(id)) {
					float scale = Math.min(1, curMonster.hb.width * 1.2f / CAMERA_WIDTH);
					scale = Math.min(scale, curMonster.hb.width * 2.0f / CAMERA_HEIGHT);
					scale = Math.max(scale, curMonster.hb.width / CAMERA_WIDTH);
					scale = Math.max(scale, curMonster.hb.height / CAMERA_HEIGHT);

					camera.viewportWidth = CAMERA_WIDTH * scale;
					camera.viewportHeight = CAMERA_HEIGHT * scale;
					camera.position.set(
							curMonster.hb.cX,
							curMonster.hb.cY - curMonster.hb.height * 0.1f + Math.max((curMonster.hb.height - CAMERA_HEIGHT * scale * Settings.scale) / 2, 0),
							0.0f);
					camera.update();
					fbo.begin();
					Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
					Gdx.gl.glColorMask(true, true, true, true);

					if (curMonster instanceof Hexaghost) {
						HexaghostBody body = ReflectionHacks.getPrivate(curMonster, Hexaghost.class, "body");
						Matrix4 tmp = sb.getProjectionMatrix().cpy();
						sb.setProjectionMatrix(camera.combined);
						sb.begin();
						if (body != null) {
							body.render(sb);
						}

						Texture img = ReflectionHacks.getPrivate(curMonster, AbstractMonster.class, "img");
						if (img != null) {
							sb.draw(img,
									curMonster.drawX - img.getWidth() * Settings.scale / 2.0F + curMonster.animX,
									curMonster.drawY + curMonster.animY, img.getWidth() * Settings.scale,
									img.getHeight() * Settings.scale, 0, 0, img.getWidth(), img.getHeight(), curMonster.flipHorizontal, curMonster.flipVertical);
						}
						for (AbstractGameEffect effect : AbstractDungeon.effectList) {
							if (effect instanceof GhostlyFireEffect || effect instanceof GhostlyWeakFireEffect) {
								effect.render(sb);
							}
						}
						sb.end();
						sb.setProjectionMatrix(tmp);
					} else {
						Skeleton skeleton = ReflectionHacks.getPrivate(curMonster, AbstractCreature.class, "skeleton");
						if (skeleton != null) {
							Matrix4 tmp = CardCrawlGame.psb.getProjectionMatrix().cpy();
							CardCrawlGame.psb.setProjectionMatrix(camera.combined);
							CardCrawlGame.psb.begin();
							AbstractCreature.sr.draw(CardCrawlGame.psb, skeleton);
							CardCrawlGame.psb.end();
							CardCrawlGame.psb.setProjectionMatrix(tmp);
						} else {
							Texture img = ReflectionHacks.getPrivate(curMonster, AbstractMonster.class, "img");
							if (img != null) {
								Matrix4 tmp = sb.getProjectionMatrix().cpy();
								sb.setProjectionMatrix(camera.combined);
								sb.begin();
								sb.draw(img,
										curMonster.drawX - img.getWidth() * Settings.scale / 2.0F + curMonster.animX,
										curMonster.drawY + curMonster.animY, img.getWidth() * Settings.scale,
										img.getHeight() * Settings.scale, 0, 0, img.getWidth(), img.getHeight(), curMonster.flipHorizontal, curMonster.flipVertical);
								sb.end();
								sb.setProjectionMatrix(tmp);
							}
						}
					}

					Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fbo.getWidth(), fbo.getHeight());
					Texture texture = new Texture(pixmap);
					pixmap.dispose();
					fbo.end();

					fbo.begin();
					sb.setShader(Shader.tintGradientShader);
					switch (curMonster.intent) {
						case ATTACK:
							sb.setColor(1.0f, 0.0f, 0.0f, 1.0f);
							break;
						case BUFF:
							sb.setColor(0.0f, 1.0f, 0.0f, 1.0f);
							break;
						case MAGIC:
						case DEBUFF:
						case STRONG_DEBUFF:
							sb.setColor(0.3f, 1.0f, 0.6f, 1.0f);
							break;
						case DEFEND:
							sb.setColor(0.0f, 0.0f, 1.0f, 1.0f);
							break;
						case SLEEP:
						case ESCAPE:
						case UNKNOWN:
							sb.setColor(1.0f, 1.0f, 1.0f, 1.0f);
							break;
						case ATTACK_BUFF:
							sb.setColor(0.75f, 0.75f, 0.0f, 1.0f);
							break;
						case DEFEND_BUFF:
							sb.setColor(0.0f, 0.75f, 0.75f, 1.0f);
							break;
						case ATTACK_DEBUFF:
							sb.setColor(0.85f, 0.6f, 0.4f, 1.0f);
							break;
						case DEFEND_DEBUFF:
							sb.setColor(0.2f, 0.6f, 0.95f, 1.0f);
							break;
						case ATTACK_DEFEND:
							sb.setColor(0.75f, 0.0f, 0.75f, 1.0f);
							break;
						default:
							switch (curCard.type) {
								case ATTACK:
									sb.setColor(1.0f, 0.0f, 0.0f, 1.0f);
									break;
								case POWER:
									sb.setColor(0.0f, 1.0f, 0.0f, 1.0f);
									break;
								default:
									sb.setColor(0.0f, 0.0f, 1.0f, 1.0f);
							}
					}
					sb.begin();
					Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
					sb.draw(texture, 0, 0, WIDTH, HEIGHT);
					sb.setShader(null);
					sb.end();

					pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fbo.getWidth(), fbo.getHeight());
					fbo.end();

					AbstractMonsterCard.saveTexture(id, pixmap);
					curCard.loadTexture(id);
				}
			} while (false);

			curMonster = null;
			curCard = null;
		}
	}

	private static class BeforeTakeTurnLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "intent");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	@SpirePatch(clz = GameActionManager.class, method = "addToBottom")
	@SpirePatch(clz = GameActionManager.class, method = "addToTop")
	public static class ActionManagerCapture {
		@SpirePrefixPatch
		public static void Prefix(GameActionManager __instance, AbstractGameAction action) {
			if (action instanceof SuicideAction) {
				suicide = true;
				return;
			}
			if (curCard instanceof DynamicCard) {
				DynamicCard genCard = (DynamicCard) curCard;
				if (action instanceof GainBlockAction || action instanceof GainBlockRandomMonsterAction) {
					genCard.baseBlock = action.amount;
				} else if (action instanceof ApplyPowerAction) {
					AbstractPower p = ReflectionHacks.getPrivate(action, ApplyPowerAction.class, "powerToApply");
					String s = powerCodeMap.get(p.ID);
					if (s != null) {
						int amount = p.amount;
						int i = 0;
						if (s.charAt(0) >= '2' && s.charAt(0) <= '9') {
							amount *= (s.charAt(0) - '0');
							i = 1;
						}
						if (action.target instanceof AbstractFriendlyMonster || action.target instanceof AbstractPlayer) {
							for (int len = s.length(); i < len; i++) {
								char c = s.charAt(i);
								if (c == 'D') {
									genCard.addDebuff('V');
								} else if (c == 'F') {
									frail = true;
								} else if (c == 'H') {
									genCard.isDraw = true;
									amount = Math.max(Math.abs(amount), 2);
								} else {
									genCard.addDebuff(c);
								}
							}
							genCard.setMagicNumber(amount);
						} else if (action.target instanceof AbstractMonster) {
							for (int len = s.length(); i < len; i++) {
								char c = s.charAt(i);
								genCard.addBuff(c);
							}
							genCard.setMagicNumber(amount);
						}
					}
				} else if (action instanceof MakeTempCardInDiscardAction
						|| action instanceof MakeTempCardInDrawPileAction
						|| action instanceof MakeTempCardInDiscardAndDeckAction) {
					AbstractCard c;
					int numCards;
					if (action instanceof MakeTempCardInDiscardAction) {
						c = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAction.class, "c");
						numCards = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAction.class, "numCards");
					} else if (action instanceof MakeTempCardInDrawPileAction) {
						c = ReflectionHacks.getPrivate(action, MakeTempCardInDrawPileAction.class, "cardToMake");
						numCards = action.amount;
					} else {
						c = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAndDeckAction.class, "cardToMake");
						numCards = 2;
					}
					if (c instanceof Dazed) {
						genCard.isDraw = true;
						genCard.setMagicNumber(numCards);
					} else if (c instanceof Wound || c instanceof Slimed) {
						genCard.exhaustOther = true;
						if (numCards >= 3) {
							genCard.isDraw = true;
							genCard.setMagicNumber(3);
						}
					} else if (c instanceof VoidCard) {
						genCard.nextTurnEnergy = true;
					} else if (c instanceof Burn) {
						genCard.addDebuff('P');
						genCard.setMagicNumber(numCards * 2);
					}
				} else if (action instanceof VampireDamageAction) {
					genCard.isVampire = true;
				}
			}
		}
	}

	@SpirePatch(clz = SpawnMonsterAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractMonster.class, boolean.class, int.class})
	public static class SpawnMonsterCapture {
		@SpirePostfixPatch
		public static void Postfix(SpawnMonsterAction __instance, AbstractMonster m, boolean isMinion, int slot) {
			if (!isMinion) return;
			boolean setSummon = false;
			SummonCard sCard = null;
			if (curCard instanceof DynamicCard) {
				sCard = new SummonCard();
				sCard.setName(hasUniqueName ? curCard.originalName : null, m);
				setSummon = true;
				curCard = sCard;
			} else if (curCard instanceof SummonCard) {
				sCard = (SummonCard) curCard;
				if (!sCard.summonID.equals(m.id)) {
					setSummon = AbstractDungeon.miscRng.randomBoolean();
				}
			}
			if (setSummon) {
				sCard.setSummon(m, 1, 16);
			}
			if (suicide) {
				lastMoveCards.put(m, curCard);
			}
		}
	}
}
