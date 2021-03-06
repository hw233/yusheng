package l1j.server.data.item_etcitem.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import l1j.server.data.cmd.Skill_Check;
import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>精灵水晶(精灵魔法-火属性)</font><BR>
 * Spirit Crystal
 * 
 * @author dexc
 * 
 */
public class Skill_SpiritCrystal_Fire extends ItemExecutor {

    /**
	 *
	 */
    private Skill_SpiritCrystal_Fire() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Skill_SpiritCrystal_Fire();
    }

    /**
     * 道具物件执行
     * 
     * @param data
     *            参数
     * @param pc
     *            执行者
     * @param item
     *            物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc,
            final L1ItemInstance item) {
        // 例外状况:物件为空
        if (item == null) {
            return;
        }
        // 例外状况:人物为空
        if (pc == null) {
            return;
        }
        // 不是精灵
        if (!pc.isElf()) {
            // 79 没有任何事情发生
            final S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);

            // 属性系不同
        } else if (pc.getElfAttr() != 2) {
            // 684 属性系列不同无法学习。
            final S_ServerMessage msg = new S_ServerMessage(684);
            pc.sendPackets(msg);

        } else {
            // 取得名称
            final String nameId = item.getItem().getNameId();
            // 技能编号
            int skillid = 0;
            // 技能属性 0:中立属性魔法 1:正义属性魔法 2:邪恶属性魔法
            // 技能属性 3:精灵专属魔法 4:王族专属魔法 5:骑士专属技能 6:黑暗精灵专属魔法
            final int attribute = 3;
            // 分组
            int magicLv = 0;

            if (nameId.equalsIgnoreCase("$1837")) {// 精灵水晶(火焰武器)
                // 技能编号
                skillid = FIRE_WEAPON;
                // 分组
                magicLv = 13;

            } else if (nameId.equalsIgnoreCase("$1844")) {// 精灵水晶(烈炎气息)
                // 技能编号
                skillid = FIRE_BLESS;
                // 分组
                magicLv = 14;

            } else if (nameId.equalsIgnoreCase("$1851")) {// 精灵水晶(烈炎武器)
                // 技能编号
                skillid = BURNING_WEAPON;
                // 分组
                magicLv = 15;

            } else if (nameId.equalsIgnoreCase("$3267")) {// 精灵水晶(属性之火)
                // 技能编号
                skillid = ELEMENTAL_FIRE;
                // 分组
                magicLv = 15;

            } else if (nameId.equalsIgnoreCase("$4714")) {// 精灵水晶(烈焰之魂)
                // 技能编号
                skillid = SOUL_OF_FLAME;
                // 分组
                magicLv = 15;

            } else if (nameId.equalsIgnoreCase("$4715")) {// 精灵水晶(能量激发)
                // 技能编号
                skillid = ADDITIONAL_FIRE;
                // 分组
                magicLv = 15;

            }

            // 检查学习该法术是否成立
            Skill_Check.check(pc, item, skillid, magicLv, attribute);
        }
    }
}
