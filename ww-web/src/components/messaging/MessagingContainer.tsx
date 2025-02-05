import MessageInput from "@/components/messaging/MessageInput";
import Messages from "@/components/messaging/Messages";
import { Stack } from "@chakra-ui/react";

const MessagingContainer = () => {
  return (
    <Stack height="100%">
      <Messages />
      <MessageInput />
    </Stack>
  );
};

export default MessagingContainer;
