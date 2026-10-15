package client.cn.nsimu.simukraft.client.citizen;

import client.cn.nsimu.simukraft.client.city.CityCoreScreenOpener;
import common.cn.nsimu.simukraft.network.citizen.manage.CityCitizenManageResponsePacket;

public final class CityCitizenManageScreen {
    private CityCitizenManageScreen() {
    }

    public static void open(CityCitizenManageResponsePacket packet) {
        CityCoreScreenOpener.openCitizens(packet);
    }
}
