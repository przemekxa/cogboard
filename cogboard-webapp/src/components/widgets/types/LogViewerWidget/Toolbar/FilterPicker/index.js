import React, { useState } from 'react';
import { setFilters } from './helpers';

import logLevels from '../../logLevels';

import {
  Select,
  Chip,
  MenuItem,
  FormControl,
  InputLabel
} from '@material-ui/core';
import { ScrollableBox } from './styled';
import ToolbarGroup from '../ToolbarGroup';
import AdvancedFiltersMenu from './AdvancedFiltersMenu';

const FilterPicker = ({ widgetLocalStorage }) => {
  const regExpFilters = widgetLocalStorage.get()?.regExpFilters || [];
  const [logLevel, setLogLevel] = useState('info');

  const handleSelection = selectedList =>
    setFilters(
      widgetLocalStorage,
      regExpFilters.map(filter => ({
        ...filter,
        checked: selectedList.map(({ id }) => id).includes(filter.id)
      }))
    );

  const handleDelete = id =>
    setFilters(
      widgetLocalStorage,
      regExpFilters.map(filter =>
        filter.id === id ? { ...filter, checked: !filter.checked } : filter
      )
    );

  return (
    <ToolbarGroup title="Filters">
      <FormControl>
        <InputLabel id="filters-label">Filters</InputLabel>
        <Select
          id="filters"
          labelId="filters-label"
          multiple
          style={{ width: '200px' }}
          value={regExpFilters.filter(filter => filter.checked)}
          size="small"
          onChange={e => handleSelection(e.target.value)}
          renderValue={selected => (
            <ScrollableBox>
              {selected.map(({ id, label }) => (
                <Chip
                  key={id}
                  label={label}
                  onDelete={() => handleDelete(id)}
                  onMouseDown={e => e.stopPropagation()}
                  style={{ marginRight: '4px' }}
                  size="small"
                />
              ))}
            </ScrollableBox>
          )}
        >
          {regExpFilters.map(filter => (
            <MenuItem key={filter.id} value={filter}>
              {filter.label}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl>
        <InputLabel id="log-level-label">Log level</InputLabel>
        <Select
          labelId="log-level-label"
          label="Log level"
          style={{ width: '100px' }}
          value={logLevel}
          onChange={e => setLogLevel(e.target.value)}
        >
          {logLevels.map((level, index) => (
            <MenuItem key={index} value={level.value}>
              {level.value.toUpperCase()}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      <AdvancedFiltersMenu widgetLocalStorage={widgetLocalStorage} />
    </ToolbarGroup>
  );
};

export default FilterPicker;
