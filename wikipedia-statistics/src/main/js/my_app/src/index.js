import React from "react";
import ReactDOM from "react-dom";
import "./css/App.css";
import { HashRouter} from "react-router-dom";
import Stats from "./stats";
import { ChakraProvider } from "@chakra-ui/react";
import theme from "./theme/theme";
import { QueryClientProvider, QueryClient } from 'react-query'

const queryClient = new QueryClient();

ReactDOM.render(
  <ChakraProvider theme={theme}>
    <QueryClientProvider client={queryClient}>
      <React.StrictMode>
        <HashRouter>
          <Stats />
        </HashRouter>
      </React.StrictMode>
    </QueryClientProvider>
  </ChakraProvider>,
  document.getElementById("root")
);


  