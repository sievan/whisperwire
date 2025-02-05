import MessageInput from "@/components/messaging/MessageInput";
import Messages from "@/components/messaging/Messages";
import { chakra, Stack } from "@chakra-ui/react";

const MessagingContainer = chakra(({ className }: { className?: string }) => {
  return (
    <Stack className={className} height="100%">
      <Messages flex={1} />
      <MessageInput />
    </Stack>
  );
});

export default MessagingContainer;
