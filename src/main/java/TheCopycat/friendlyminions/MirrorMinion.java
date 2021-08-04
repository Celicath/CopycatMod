package TheCopycat.friendlyminions;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.CopycatSuicideAction;
import TheCopycat.actions.SummonCopycatMinionAction;
import TheCopycat.cards.monster.DynamicCard;
import TheCopycat.crossovers.DTModCrossover;
import TheCopycat.patches.CaptureEnemyMovePatch;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.BobEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class MirrorMinion extends AbstractCopycatMinion {
	private static final String RAW_ID = "MirrorMinion";
	private static final String ID = CopycatModMain.makeID(RAW_ID);
	public AbstractMonster origMonster;
	public static boolean disableMonsterLogic = false;

	public boolean endBattle = false;

	public MirrorMinion(String name, AbstractMonster m, int maxHealth) {
		super(name, ID, maxHealth, m.hb_w / Settings.scale, m.hb_h / Settings.xScale, CopycatModMain.GetMinionPath(RAW_ID));
		disableMonsterLogic = true;
		hb_x = m.hb_x;
		hb_y = m.hb_y;

		origMonster = m;
		m.flipHorizontal = true;

		Random temp = AbstractDungeon.aiRng;
		AbstractDungeon.aiRng = BetterFriendlyMinionsUtils.minionAiRng;
		m.rollMove();
		AbstractDungeon.aiRng = temp;
		m.createIntent();
		m.update();
		m.tint.color.a = 0.0f;
		m.hbAlpha = 0;
		CaptureEnemyMovePatch.sb.begin();
		m.render(CaptureEnemyMovePatch.sb);
		CaptureEnemyMovePatch.sb.end();

		disableMonsterLogic = false;
	}

	public MirrorMinion(AbstractMonster m) {
		super(m.name, ID, m.maxHealth, m.hb_w / Settings.scale, m.hb_h / Settings.xScale, CopycatModMain.GetMinionPath(RAW_ID));
		disableMonsterLogic = true;

		currentHealth = m.currentHealth;
		m.hb_x = -m.hb_x;
		hb_x = m.hb_x;
		hb_y = m.hb_y;

		origMonster = m;
		m.flipHorizontal = true;
		powers = m.powers;
		powers.removeIf(p -> p.ID.equals(MinionPower.POWER_ID));
		m.powers = new ArrayList<>();
		BetterFriendlyMinionsUtils.switchTarget(origMonster, null, true);

		disableMonsterLogic = false;
	}

/* TODO: delete this
	@Override
	public void createIntent() {
		ReflectionHacks.setPrivate(this, EnemyMoveInfo.class, "move",
				ReflectionHacks.getPrivate(origMonster, EnemyMoveInfo.class, "move"));
		super.createIntent();
	}
*/

	@Override
	public void useFastAttackAnimation() {
		super.useFastAttackAnimation();
		origMonster.useFastAttackAnimation();
	}

	@Override
	public void setIndex(int index) {
		super.setIndex(index);

		origMonster.drawX = drawX;
		origMonster.drawY = drawY;
	}

	@Override
	public void update() {
		super.update();
		if (!endBattle) {
			origMonster.isDying = false;
		}
		origMonster.update();
		origMonster.isDying = true;
		origMonster.hbAlpha = 0;
	}

	@Override
	public void render(SpriteBatch sb) {
		super.render(sb);
		origMonster.isDying = false;
		origMonster.render(sb);
		origMonster.isDying = true;

		if (origMonster.intentAlpha != 0.0f) {
			int count = getMoveCount();
			if (count > 1) {
				FontHelper.renderFontCentered(sb,
						FontHelper.topPanelInfoFont,
						"(x" + count + ")",
						origMonster.intentHb.cX,
						origMonster.intentHb.cY + ReflectionHacks.<BobEffect>getPrivate(origMonster, AbstractMonster.class, "bobEffect").y + 20.0F * Settings.scale,
						new Color(1.0f, 1.0f, 1.0f, origMonster.intentAlpha));
			}
		}
	}

	@Override
	public void doMove(AbstractMonster target) {
		BetterFriendlyMinionsUtils.hijackActionQueue(target, BetterFriendlyMinionsUtils.HijackMode.FRIENDLY_MINION_ATTACK);
		origMonster.takeTurn();
		BetterFriendlyMinionsUtils.revertHijack();

		ArrayList<AbstractGameAction> newActions = AbstractDungeon.actionManager.actions;
		HashMap<String, Boolean> appliedID = new HashMap<>();
		for (int i = 0; i < newActions.size(); i++) {
			AbstractGameAction a = newActions.get(i);
			if (a instanceof DamageAction) {
				DamageInfo info = ReflectionHacks.getPrivate(a, DamageAction.class, "info");
				info.applyPowers(this, target);
				info.owner = this;
				a.source = this;
				a.target = target;
			} else if (a instanceof ApplyPowerAction) {
				AbstractPower power = ReflectionHacks.getPrivate(a, ApplyPowerAction.class, "powerToApply");

				newActions.remove(i--);

				if (a.target instanceof AbstractPlayer) {
					String code = CaptureEnemyMovePatch.powerCodeMap.get(power.ID);
					if (code != null) {
						int multiplier = 1;
						for (int j = 0; j < code.length(); j++) {
							char ch = code.charAt(j);
							if (ch >= '2' && ch <= '9') {
								multiplier = ch - '0';
							} else if (ch == 'E') {
								AbstractDungeon.actionManager.actions.add(++i, new ApplyPowerAction(target, this, new StrengthPower(target, -10)));
								if (!target.hasPower(ArtifactPower.POWER_ID)) {
									AbstractDungeon.actionManager.actions.add(++i, new ApplyPowerAction(target, this, new GainStrengthPower(target, 10)));
								}
							} else {
								AbstractPower newPower = DynamicCard.getDebuffPower(target, this, ch, power.amount * multiplier);
								if (newPower != null) {
									AbstractDungeon.actionManager.actions.add(++i, new ApplyPowerAction(target, this, newPower));
								}
							}
						}
					}
				} else {
					ArrayList<AbstractCreature> targets = new ArrayList<>();
					if (a.target == origMonster) {
						targets.add(this);
					} else if (!appliedID.containsKey(power.ID)) {
						targets.add(AbstractDungeon.player);
						appliedID.put(power.ID, false);
					} else {
						for (AbstractMonster fm : BetterFriendlyMinionsUtils.getMinionList()) {
							if (this != fm) {
								targets.add(fm);
							}
						}
						if (CopycatModMain.isDragonTamerLoaded) {
							targets.add(DTModCrossover.getLivingDragon());
						}
						appliedID.put(power.ID, true);
					}
					String code = CaptureEnemyMovePatch.powerCodeMap.get(power.ID);
					if (code != null) {
						for (AbstractCreature c : targets) {
							if (c == null || c.isDead)
								for (int j = 0; j < code.length(); j++) {
									AbstractPower newPower = DynamicCard.getBuffPower(c, code.charAt(j), power.amount);
									if (newPower != null) {
										AbstractDungeon.actionManager.actions.add(++i, new ApplyPowerAction(c, this, newPower));
									}
								}
						}
					}
				}
			} else if (a instanceof GainBlockAction) {
				newActions.remove(i--);
				ArrayList<AbstractCreature> targets = new ArrayList<>();
				if (a.target == origMonster) {
					targets.add(this);
				} else if (!appliedID.containsKey(CopycatModMain.makeID("BLOCK"))) {
					targets.add(AbstractDungeon.player);
					appliedID.put(CopycatModMain.makeID("BLOCK"), false);
				} else {
					for (AbstractMonster fm : BetterFriendlyMinionsUtils.getMinionList()) {
						if (this != fm) {
							targets.add(fm);
						}
					}
					appliedID.put(CopycatModMain.makeID("BLOCK"), true);
				}
				for (AbstractCreature c : targets) {
					AbstractDungeon.actionManager.actions.add(++i, new GainBlockAction(c, a.amount));
				}
			} else if (a instanceof SpawnMonsterAction) {
				AbstractMonster monster = ReflectionHacks.getPrivate(a, SpawnMonsterAction.class, "m");
				newActions.remove(i--);
				AbstractDungeon.actionManager.actions.add(++i, new SummonCopycatMinionAction(new MirrorMinion(
						monster.name,
						monster,
						Math.min(monster.maxHealth, 16)
				)));
			} else if (a instanceof LoseHPAction) {
				a.target = a.source = this;
			} else if (a instanceof SuicideAction) {
				newActions.remove(i--);
				AbstractDungeon.actionManager.actions.add(++i, new CopycatSuicideAction(this));
			} else if (a instanceof MakeTempCardInDiscardAction || a instanceof MakeTempCardInDrawPileAction || a instanceof MakeTempCardInDiscardAndDeckAction) {
				newActions.remove(i--);
			}
		}

		newActions.addAll(BetterFriendlyMinionsUtils.origActions);
	}

	@Override
	public void applyPowers() {
		ArrayList<AbstractPower> playerPowers = AbstractDungeon.player.powers;
		AbstractDungeon.player.powers = dummyPowers;
		ArrayList<AbstractPower> origMonsterPowers = origMonster.powers;
		origMonster.powers = powers;
		origMonster.applyPowers();
		AbstractDungeon.player.powers = playerPowers;
		origMonster.powers = origMonsterPowers;
	}
}
