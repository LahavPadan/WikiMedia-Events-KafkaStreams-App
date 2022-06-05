import React from "react";
import { Flex, Text } from "@chakra-ui/react";
import Card from "./components/card/Card.js";
import TableTopCreators from "./TableTopCreators";
import tableDataTopCreators from "./variables/tableDataTopCreators.json";
import { tableColumnsTopCreators } from "./variables/tableColumnsTopCreators";

function MostActive(props) {
  const { options, ...rest } = props;

  return (
    <Flex 
    flexDirection='column'
    gridArea={{ xl: "1 / 3 / 2 / 4", "2xl": "1 / 2 / 2 / 3" }}
    {...rest}>
        
        {options}

        <Card px='0px' mb='20px'>
            <TableTopCreators
            title = {"Most Active Users"}
            tableData={tableDataTopCreators}
            columnsData={tableColumnsTopCreators}
            />
        </Card>

        <Card px='0px' mb='20px'>
            <TableTopCreators
            title={"Most Active Pages"}
            tableData={tableDataTopCreators}
            columnsData={tableColumnsTopCreators}
            />
        </Card>

    </Flex>
  );
}

export default MostActive;
