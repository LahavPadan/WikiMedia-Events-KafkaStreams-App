import wikiLanguages from './wikiLanguages.json';
import React, {useState, useCallback } from "react";
import {
  Flex,
  Box,
  Heading,
  SimpleGrid,
  Grid,
  Menu,
  MenuButton, 
  MenuItem,
  MenuList,
  Link,
  Button,
  useColorModeValue,
  Text, 
} from "@chakra-ui/react";




import { ChevronDownIcon } from '@chakra-ui/icons'
import AutoCompleteOptions from "./components/AutoCompleteOptions.js"
import CardWithOptions from "./components/card/CardWithOptions.js"
import MostActive from "./MostActive.js"
import PieCard from "./PieCard";

import { useQueries } from 'react-query';
import axios from 'axios';

const refetchInterval = 30 * 1000;


// const updateMostActive = useCallback((type) => {
//   results.filter(result => result.queryKey.includes(type)).forEach(result => result.refetch());
// }, [results]);

// const updateAll = useCallback(() => {
//   results.forEach(result => result.refetch());
// }, [results]);


export default function Stats() {
  let mainText = useColorModeValue("navy.700", "white");
  const [timeWindow, setTimeWindow] = useState(["Week"]);
  const [language, setLanguage]  = useState(['English']);
  const [userType, setUserType]  = useState(['Human']);
  // const results = useQueries(...queries);
  // if (results.some(result => result.isLoading))
  // {
  //   return <Header>Loading... </Header>
  // }

  // if (results.some(result => result.isError))
  // {
  //   return <Header> {results.forEach(result => result.error.message)} </Header>
  // }

  
  const fetchFromAPI = (route)=>{
    return axios.get(`https://localhost:4000/${timeWindow}/${route}`);
  }

  const queryPieChartByLang = (pieChartType)=>{
    queries = [];
    for (let lang of Object.values(wikiLanguages)){
      queries.push(
        {
          queryKey: [pieChartType+"ByLang", pieChartType+"ByLang"],
          queryFn: () => fetchFromAPI(`${pieChartType}/language/${lang}`),
          refetchInterval: refetchInterval
        }
      );
    }
    return queries;
  }

  const queryPieChartByUserType = (pieChartType)=>{
    queries = [];
    for(let userType in ['user', 'bot']){
      queries.push(
        {
          queryKey: [pieChartType+"ByUserType", pieChartType+"ByUserType"],
          queryFn: () => fetchFromAPI(`${pieChartType}/userType/${userType}`),
          refetchInterval: refetchInterval
        }
      );
    }
    return queries;
  }

  let queries = [
    {
      queryKey: ["mostActivePagesByLang", "mostActivePagesByLang"],
      queryFn: () => fetchFromAPI(`mostActivePages/language/${language}`),
      refetchInterval: refetchInterval
    },
    {
      queryKey: ["mostActiveUsersByLang", "mostActiveUsersByLang"],
      queryFn: () => fetchFromAPI(`mostActiveUsers/language/${language}`),
      refetchInterval: refetchInterval 
    },
    {
      queryKey: ["mostActiveUsersByUserType", "mostActiveUsersByUserType"],
      queryFn: () => fetchFromAPI(`mostActiveUsers/userType/${userType}`),
      refetchInterval: refetchInterval
    },
  ].concat(queryPieChartByLang("pagesCreated"))
  .concat(queryPieChartByUserType("pagesCreated"))
  .concat(queryPieChartByLang("pagesModified"))
  .concat(queryPieChartByUserType("pagesModified"));



  // Menu Options
  let timeWindowOptions = [];
  for(let time of ['Hour', 'Day', 'Week', 'Month']){
    timeWindowOptions.push(
      <MenuItem onClick={()=>{setTimeWindow(time);}}> {time} </MenuItem>);
  } 

  let langOptions = [];
  for (let key of Object.keys(wikiLanguages)){
    let lang = wikiLanguages[key];
    langOptions.push(
      <MenuItem onClick={()=>setLanguage(lang)}> {lang} </MenuItem>
    );
  }


  return (
      
  <Box mt={{ base: "130px", md: "80px", xl: "80px" }} ml={{ base: "130px", md: "80px", xl: "80px" }}>
    {/* Header */}
    <Link
      color={mainText}
      href='#'
      bg='inherit'
      borderRadius='inherit'
      fontWeight='bold'
      fontSize='34px'
      _hover={{ color: { mainText } }}
      _active={{
        bg: "inherit",
        transform: "none",
        borderColor: "transparent",
      }}
      _focus={{
        boxShadow: "none",
      }}>
      Wiki Stats
    </Link>

    <Menu>
      <MenuButton as={Button} rightIcon={<ChevronDownIcon />}>
        {timeWindow}
      </MenuButton>
      <MenuList>
        {timeWindowOptions}
      </MenuList>
    </Menu>


    {/* Main Content */}
    <Flex
        mt='20px'
        gap={{ base: "20px", xl: "20px" }}
        flexDirection= 'row'
        justifyContent='space-around'>

      {/* Pie Charts */}
      <Flex
        flexDirection='column'
        flexShrink={0}>

        <CardWithOptions
          options={<AutoCompleteOptions title={'Select Language'}
            iterable={Object.values(wikiLanguages)} 
            onChange={(lang)=>setLanguage(lang)}/>}
        >
          <SimpleGrid columns={{ base: 1, md: 2 }} gap='20px'>
            <PieCard title={"Pages Created"}/>
            <PieCard title={"Pages Modified"}/>
          </SimpleGrid>
        </CardWithOptions> 

        <CardWithOptions mt='50px'
          options={<AutoCompleteOptions title={'Select User Type'}
            iterable={Object.values(wikiLanguages)} 
            onChange={(lang)=>setLanguage(lang)}/>}
        >
          <SimpleGrid columns={{ base: 1, md: 2 }} gap='20px'>
            <PieCard title={"Pages Created"}/>
            <PieCard title={"Pages Modified"}/>
          </SimpleGrid>
        </CardWithOptions>
      </Flex>


      <CardWithOptions>
      <MostActive 
        options={<AutoCompleteOptions title={'Select Language'}
        iterable={Object.values(wikiLanguages)} 
        onChange={(lang)=>setLanguage(lang)}/>}
        />
      </CardWithOptions>  
      {/* <Flex
        flexDirection='column'>
        <MostActive 
        options={<AutoCompleteOptions iterable={Object.values(wikiLanguages)} 
        onChange={(lang)=>setLanguage(lang)}/>}
        />
      </Flex> */}

      <CardWithOptions>
      <MostActive
        options={<AutoCompleteOptions title={'Select User Type'} iterable={['user', 'bot']} 
        onChange={(userType)=>setUserType(userType)}/>}
        />
      </CardWithOptions> 
    </Flex>
  </Box>
  );
}