import "./index.css";
import "./App.css";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { Provider } from "@/components/ui/provider.tsx";
import { BrowserRouter, Route, Routes } from "react-router";
import Conversations from "@/routes/Conversations.tsx";
import Home from "@/routes/Home";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <Provider>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route
            path="/conversations/:conversationId"
            element={<Conversations />}
          />
        </Routes>
      </Provider>
    </BrowserRouter>
  </StrictMode>,
);
