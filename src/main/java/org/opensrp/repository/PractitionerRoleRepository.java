package org.opensrp.repository;

import org.opensrp.domain.PractitionerRole;

import java.util.List;

public interface PractitionerRoleRepository extends BaseRepository<PractitionerRole> {

    List<PractitionerRole>  getRolesForPractitioner(String practitionerIdentifier);

    public org.opensrp.domain.postgres.PractitionerRole getPractitionerRole(String id);
}