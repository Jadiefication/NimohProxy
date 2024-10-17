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
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

@Plugin(id = "nimohproxy", name = "NimohProxy", version = "1.0-SNAPSHOT")
public class NimohProxy {

    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("bungeecord:main");

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

        if (subchannel.equals("Connect")) {
            String serverName = in.readUTF();
            if (event.getSource() instanceof Player player) {
                server.getServer(serverName).ifPresent(targetServer ->
                        player.createConnectionRequest(targetServer).fireAndForget());
            }
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(IDENTIFIER);
    }
}
