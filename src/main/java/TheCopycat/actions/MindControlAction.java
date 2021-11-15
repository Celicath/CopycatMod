package TheCopycat.actions;

import TheCopycat.cards.MindControl;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.utils.BaseGamePrivateBusterUtils;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class MindControlAction extends AbstractGameAction {
	public float startX, startY;
	public float targetX, targetY;
	public float t;
	private final boolean freeToPlayOnce;
	private final int magic;
	private final AbstractPlayer p;
	private final AbstractMonster targetMonster;
	private final int energyOnUse;

	public MindControlAction(AbstractPlayer p, AbstractMonster m, int magic, boolean freeToPlayOnce, int energyOnUse) {
		duration = startDuration = Settings.FAST_MODE ? 1.25f : 1.5f;

		this.p = p;
		this.targetMonster = m;
		this.magic = magic;
		this.freeToPlayOnce = freeToPlayOnce;
		this.actionType = ActionType.SPECIAL;
		this.energyOnUse = energyOnUse;
	}

	@Override
	public void update() {
		if (duration == startDuration) {
			int effect = EnergyPanel.totalCount;
			if (energyOnUse != -1) {
				effect = energyOnUse;
			}

			AbstractRelic r = p.getRelic(ChemicalX.ID);
			if (r != null) {
				effect += 2;
				r.flash();
			}
			if (targetMonster.currentHealth > magic * effect) {
				AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, MindControl.cardStrings.EXTENDED_DESCRIPTION[0], true));
				isDone = true;
				return;
			}

			if (!freeToPlayOnce) {
				p.energy.use(EnergyPanel.totalCount);
			}

			int index = BetterFriendlyMinionsUtils.getNextCopycatMinionSlot();
			if (index == -1) {
				addToTop(new SuicideAction(targetMonster));
				isDone = true;
				return;
			}
			t = 0;
			startX = targetMonster.drawX;
			startY = targetMonster.drawY;
			targetX = AbstractCopycatMinion.calcXPos(index);
			targetY = AbstractCopycatMinion.calcYPos(index);
		}
		tickDuration();
		if (isDone) {
			MirrorMinion minion = new MirrorMinion(targetMonster);
			addToTop(new SummonCopycatMinionAction(minion));
			addToTop(new AbstractGameAction() {
				@Override
				public void update() {
					AbstractDungeon.getMonsters().monsters.remove(targetMonster);

					boolean tmp = AbstractDungeon.getCurrRoom().cannotLose;
					AbstractDungeon.getCurrRoom().cannotLose = false;

					int hp = targetMonster.currentHealth;

					targetMonster.currentHealth = 0;
					targetMonster.damage(new DamageInfo(null, 0, DamageInfo.DamageType.HP_LOSS));
					addToBot(new AbstractGameAction() {
						@Override
						public void update() {
							if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
								minion.endBattle = true;
							} else {
								AbstractDungeon.getCurrRoom().cannotLose = tmp;
							}
							targetMonster.currentHealth = hp;
							isDone = true;
						}
					});
					isDone = true;
				}
			});
		} else {
			t = (startDuration - duration) / startDuration;
			targetMonster.drawX = MathUtils.lerp(startX, targetX, t);
			targetMonster.drawY = MathUtils.lerp(startY, targetY, t);
			BaseGamePrivateBusterUtils.refreshHitboxLocation(targetMonster);
		}
	}
}
