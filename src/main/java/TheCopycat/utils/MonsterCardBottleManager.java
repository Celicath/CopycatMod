package TheCopycat.utils;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.monster.AbstractMonsterCard;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BottledFlame;
import com.megacrit.cardcrawl.relics.BottledLightning;
import com.megacrit.cardcrawl.relics.BottledTornado;

import java.util.ArrayList;

public class MonsterCardBottleManager implements CustomSavable<ArrayList<Integer>> {
	public static MonsterCardBottleManager instance;

	public MonsterCardBottleManager() {
		BaseMod.addSaveField(CopycatModMain.makeID("MonsterCardBottleManager"), this);
	}

	@Override
	public ArrayList<Integer> onSave() {
		ArrayList<Integer> result = new ArrayList<>();
		AbstractRelic r = AbstractDungeon.player.getRelic(BottledFlame.ID);
		int k = -1;
		if (r instanceof BottledFlame) {
			AbstractCard c = ((BottledFlame) r).card;
			if (c instanceof AbstractMonsterCard) {
				k = AbstractDungeon.player.masterDeck.group.indexOf(c);
			}
		}
		result.add(k);

		k = -1;
		r = AbstractDungeon.player.getRelic(BottledLightning.ID);
		if (r instanceof BottledLightning) {
			AbstractCard c = ((BottledLightning) r).card;
			if (c instanceof AbstractMonsterCard) {
				k = AbstractDungeon.player.masterDeck.group.indexOf(c);
			}
		}
		result.add(k);

		k = -1;
		r = AbstractDungeon.player.getRelic(BottledTornado.ID);
		if (r instanceof BottledTornado) {
			AbstractCard c = ((BottledTornado) r).card;
			if (c instanceof AbstractMonsterCard) {
				k = AbstractDungeon.player.masterDeck.group.indexOf(c);
			}
		}
		result.add(k);
		return result;
	}

	@Override
	public void onLoad(ArrayList<Integer> save) {
		if (save != null) {
			if (save.get(0) != -1 && save.get(0) < AbstractDungeon.player.masterDeck.size()) {
				AbstractRelic r = AbstractDungeon.player.getRelic(BottledFlame.ID);
				if (r instanceof BottledFlame) {
					BottledFlame br = (BottledFlame) r;
					br.card = AbstractDungeon.player.masterDeck.group.get(save.get(0));
					br.setDescriptionAfterLoading();
				}
			}
			if (save.get(1) != -1 && save.get(1) < AbstractDungeon.player.masterDeck.size()) {
				AbstractRelic r = AbstractDungeon.player.getRelic(BottledLightning.ID);
				if (r instanceof BottledLightning) {
					BottledLightning br = (BottledLightning) r;
					br.card = AbstractDungeon.player.masterDeck.group.get(save.get(1));
					br.setDescriptionAfterLoading();
				}
			}
			if (save.get(2) != -1 && save.get(2) < AbstractDungeon.player.masterDeck.size()) {
				AbstractRelic r = AbstractDungeon.player.getRelic(BottledTornado.ID);
				if (r instanceof BottledTornado) {
					BottledTornado br = (BottledTornado) r;
					br.card = AbstractDungeon.player.masterDeck.group.get(save.get(2));
					br.setDescriptionAfterLoading();
				}
			}
		}
	}
}
