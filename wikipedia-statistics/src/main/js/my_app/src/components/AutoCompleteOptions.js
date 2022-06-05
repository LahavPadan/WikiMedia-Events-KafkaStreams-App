import React from "react";
import{
Flex,
FormControl,
FormLabel,
FormHelperText, 
} from "@chakra-ui/react";
import {
    AutoComplete,
    AutoCompleteInput,
    AutoCompleteItem,
    AutoCompleteList,
} from "@choc-ui/chakra-autocomplete";
import PropTypes from "prop-types";

function AutoCompleteOptions(props) {
  const { title, iterable, onChange, ...rest } = props;

  return (
    <Flex justify="center" align="center" w="full">
      <FormControl {...rest}>
        <FormLabel>{title}</FormLabel>
        <AutoComplete openOnFocus selectOnFocus emphasize 
          maxSuggestions={10}
          defaultValues={['English']} 
          onChange={onChange}>
          <AutoCompleteInput variant="filled" />
          <AutoCompleteList>
              {iterable.map((value, cid) => (
              <AutoCompleteItem
                  key={`option-${cid}`}
                  value={value}
                  textTransform="capitalize">
                  {value}
              </AutoCompleteItem>
              ))}
          </AutoCompleteList>
        </AutoComplete>
      </FormControl>
    </Flex>
  );
}

export default AutoCompleteOptions;

AutoCompleteOptions.propTypes = {
  title: PropTypes.string,
};
