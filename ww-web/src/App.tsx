import "./App.css";
import MessageInput from "@/components/messaging/messageInput";
import Messages from "@/components/messaging/Messages";
import { ColorModeButton } from "@/components/ui/color-mode";

function App() {
  return (
    <>
      <Messages />
      <MessageInput />
      <ColorModeButton />
    </>
  );
}

export default App;
