import { Heading, VStack } from "@chakra-ui/react";
import { Link } from "react-router";

function Home() {
  return (
    <>
      <VStack gapY={4} marginTop={5}>
        <Heading fontSize="2xl">WhisperWire</Heading>
        <Link to="/conversations/1">Conversation 1</Link>
        <Link to="/conversations/2">Conversation 2</Link>
        <Link to="/conversations/3">Conversation 3</Link>
      </VStack>
    </>
  );
}

export default Home;
