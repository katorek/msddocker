package com.wjaronski.studies.pmd.repositories;

import com.wjaronski.studies.pmd.model.UniqueTrack;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Wojciech Jaronski
 */

public interface UniqueTrackRepository extends CrudRepository<UniqueTrack, String> {
}
