package src.main.jadiefication.nimohProxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

@Plugin(id = "nimohproxy", name = "NimohProxy", version = "1.0-SNAPSHOT")
public class NimohProxy {

    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("velocity:main");

    @Inject
    private Logger logger;
    @Inject
    private ProxyServer server;

    @Inject
    public NimohProxy(Logger logger, ProxyServer server) {
        this.logger = logger;
        this.server = server;
    }

    @Subscribe
    public void onPluginMessageFromBackEnd(PluginMessageEvent event) {
        if (!IDENTIFIER.equals(event.getIdentifier())) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subchannel = in.readUTF();
        ServerConnection source = (ServerConnection) event.getSource();
        Player player = source.getPlayer();
        if (subchannel.equals("Connect")) {
            String serverName = in.readUTF();
            server.getServer(serverName).ifPresent(targetServer -> {
                player.createConnectionRequest(targetServer).fireAndForget();
                logger.info("Connecting player " + player.getUsername() + " to server " + serverName);
            });
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("NimohProxy is enabled!");
        server.getChannelRegistrar().register(IDENTIFIER);
    }
}
