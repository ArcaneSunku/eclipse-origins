package dev.atomixsoft.solar_eclipse.client.graphics.ui.ingame;

import dev.atomixsoft.solar_eclipse.client.ClientThread;
import dev.atomixsoft.solar_eclipse.client.util.ImGuiFonts;
import dev.atomixsoft.solar_eclipse.core.event.types.SendPacketEvent;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ChatMessageBroadcast;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.ChatMessageRequest;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class ChatBox {

    private final List<String> m_ChatLines;
    private final ImString m_ChatInput;

    public ChatBox() {
        m_ChatLines = new ArrayList<>();
        m_ChatInput = new ImString(256);
    }

    public void update(ChatMessageBroadcast packet) {
        m_ChatLines.add(packet.username() + ": " + packet.message());

        while(m_ChatLines.size() >= 8)
            m_ChatLines.removeFirst();
    }

    public void render() {
        ImGui.setNextWindowPos(16, 444);
        ImGui.setNextWindowSize(468, 134);
        ImGui.pushFont(ImGuiFonts.GetFont("georgiab"));

        ImGui.begin("Chat_Box", ImGuiWindowFlags.NoDecoration |
                ImGuiWindowFlags.NoBackground |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoScrollbar);

        ImGui.beginChild("Chat_Lines", 460, 120, false);

        for(String line : m_ChatLines) {
            ImGui.pushTextWrapPos(ImGui.getCursorPosX() + 445.0f);
            ImGui.textUnformatted(line);
            ImGui.popTextWrapPos();
        }

        ImGui.endChild();

        ImGui.setCursorPos(42, 122);
        ImGui.setNextItemWidth(420);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.FrameBgActive, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);

        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);

        if(ImGui.inputText("##chatInput", m_ChatInput, ImGuiInputTextFlags.EnterReturnsTrue)) {
            String message = m_ChatInput.get().trim();

            if(!message.isEmpty()) {
                ClientThread.eventBus().post(new SendPacketEvent(new ChatMessageRequest(message)));
                m_ChatInput.set("");
            }
        }

        ImGui.popStyleVar(2);
        ImGui.popStyleColor(4);
        ImGui.end();

        ImGui.popFont();
    }

}
