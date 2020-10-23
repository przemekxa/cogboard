import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { useToggle } from '../hooks';
import { getToken } from '../utils/auth';
import { getIsAuthenticated } from '../selectors';
import { editCredential } from '../actions/thunks';
import { URL } from '../constants';

import { IconButton, Tooltip } from '@material-ui/core';
import { Build } from '@material-ui/icons';
import AppDialog from './AppDialog';
import CredentialForm from './CredentialForm';

const EditCredential = ({ id }) => {
  const dispatch = useDispatch();
  const [dialogOpened, openDialog, handleDialogClose] = useToggle();

  const [credentialData, setCredentialData] = useState();
  const isAuthenticated = useSelector(getIsAuthenticated);

  const handleAddEndpointClick = () => {
    const init = isAuthenticated
      ? { headers: { Authorization: getToken() } }
      : undefined;

    fetch(`${URL.CREDENTIALS_ENDPOINT}/${id}`, init)
      .then(response => response.json())
      .then(data => {
        setCredentialData(data);
        openDialog();
      })
      .catch(console.error);
  };

  const handleSubmit = values => {
    delete values.passwordConfirmation;

    dispatch(editCredential({ id, ...values }));
    handleDialogClose();
  };

  return (
    <>
      <Tooltip title="Edit" placement="bottom">
        <IconButton
          onClick={handleAddEndpointClick}
          data-cy="edit-credential-edit-button"
        >
          <Build />
        </IconButton>
      </Tooltip>
      <AppDialog
        disableBackdropClick={true}
        handleDialogClose={handleDialogClose}
        open={dialogOpened}
        title="Edit credential"
      >
        {credentialData && (
          <CredentialForm
            onSubmit={handleSubmit}
            handleCancel={handleDialogClose}
            id={id}
            {...credentialData}
          />
        )}
      </AppDialog>
    </>
  );
};

export default EditCredential;
