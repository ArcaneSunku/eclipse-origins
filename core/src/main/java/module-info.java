module dev.atomixsoft.solar_eclipse.core {
    requires org.apache.commons.configuration2;
    requires transitive org.apache.commons.logging;
    requires transitive org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens dev.atomixsoft.solar_eclipse.core.config to org.apache.commons.configuration2;
    opens dev.atomixsoft.solar_eclipse.core.logging to org.apache.commons.logging;

    exports dev.atomixsoft.solar_eclipse.core.game;
    exports dev.atomixsoft.solar_eclipse.core.game.map;
    exports dev.atomixsoft.solar_eclipse.core.game.character;

    exports dev.atomixsoft.solar_eclipse.core.event;
    exports dev.atomixsoft.solar_eclipse.core.event.types;
    exports dev.atomixsoft.solar_eclipse.core.event.interfaces;

    exports dev.atomixsoft.solar_eclipse.core.utils;
    exports dev.atomixsoft.solar_eclipse.core.config;
    exports dev.atomixsoft.solar_eclipse.core.logging;
    //exports dev.atomixsoft.solar_eclipse.core.network;
}
