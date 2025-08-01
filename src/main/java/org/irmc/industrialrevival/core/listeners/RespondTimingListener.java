package org.irmc.industrialrevival.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.irmc.industrialrevival.api.events.ir.TickDoneEvent;
import org.irmc.industrialrevival.implementation.IndustrialRevival;


public class RespondTimingListener implements Listener {
    @EventHandler
    public void onTickDone(TickDoneEvent event) {
        IndustrialRevival.getInstance().getRunningProfilerService().respondToTimingView(IndustrialRevival.getInstance().getRunningProfilerService().pullTimingViewRequest());
    }
}
