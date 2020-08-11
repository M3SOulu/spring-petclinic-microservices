/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.customers.web;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customers.model.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@RequestMapping("/owners")
@RestController
@Timed("petclinic.owner")
@RequiredArgsConstructor
@Slf4j
class OwnerResource {

    private final OwnerRepository ownerRepository;
    private final String peopleHost = "http://people-service:8084";
    private final String petsHost = "http://pets-service:8085";

    /**
     * Create Owner
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Owner createOwner(@Valid @RequestBody Owner owner) {
        final String uri = peopleHost + "/people";
        RestTemplate restTemplate = new RestTemplate();
        People people = new People();
        people.setFirstName(owner.getFirstName());
        people.setLastName(owner.getLastName());
        People result = restTemplate.postForObject( uri, people, People.class);
        log.info("Saving people {}", result);
        return ownerRepository.save(owner);
    }

    /**
     * Read single Owner
     */
    @GetMapping(value = "/{ownerId}")
    public Optional<Owner> findOwner(@PathVariable("ownerId") int ownerId) {
        return ownerRepository.findById(ownerId);
    }

    /**
     * Read List of Owners
     */
    @GetMapping
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    /**
     * Update Owner
     */
    @PutMapping(value = "/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOwner(@PathVariable("ownerId") int ownerId, @Valid @RequestBody Owner ownerRequest) {
        final Optional<Owner> owner = ownerRepository.findById(ownerId);

        final Owner ownerModel = owner.orElseThrow(() -> new ResourceNotFoundException("Owner "+ownerId+" not found"));
        // This is done by hand for simplicity purpose. In a real life use-case we should consider using MapStruct.
        ownerModel.setFirstName(ownerRequest.getFirstName());
        ownerModel.setLastName(ownerRequest.getLastName());
        ownerModel.setCity(ownerRequest.getCity());
        ownerModel.setAddress(ownerRequest.getAddress());
        ownerModel.setTelephone(ownerRequest.getTelephone());
        log.info("Saving owner {}", ownerModel);
        ownerRepository.save(ownerModel);
    }

    @GetMapping("/petTypes")
    public List<PetType> getPetTypes() {
        final String uri = petsHost + "/petTypes";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PetType[]> responseEntity = restTemplate.getForEntity( uri, PetType[].class);
        List<PetType> result = Arrays.asList(responseEntity.getBody());
        return result;
    }

    @PostMapping("/owners/{ownerId}/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public Pet processCreationForm(
        @RequestBody PetRequest petRequest,
        @PathVariable("ownerId") String ownerId) {

        final String uri = petsHost + "/owners/" + ownerId + "/pets";
        RestTemplate restTemplate = new RestTemplate();
        Pet result = restTemplate.postForObject( uri, petRequest, Pet.class);
        log.info("Saving pets {}", result);
        return result;
    }

    @PutMapping("/owners/*/pets/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void processUpdateForm(@RequestBody PetRequest petRequest, @PathVariable("petId") int petId) {
        final String uri = petsHost + "/pets/" + petId;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put( uri, petRequest);
    }

    @GetMapping("/owners/*/pets/{petId}")
    public PetDetails findPet(@PathVariable("petId") int petId) {
        final String uri = petsHost + "/pets/" + petId;
        RestTemplate restTemplate = new RestTemplate();
        PetDetails result = restTemplate.getForObject( uri, PetDetails.class);
        return result;
    }
}
