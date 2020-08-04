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
package org.springframework.samples.petclinic.people.web;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.people.model.People;
import org.springframework.samples.petclinic.people.model.PeopleRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Jianwen Xu
 */
@RequestMapping("/people")
@RestController
@RequiredArgsConstructor
class PeopleResource {

    private final PeopleRepository peopleRepository;

    @GetMapping
    public List<People> showResourcesPeopleList() {
        return peopleRepository.findAll();
    }

    /**
     * Create People
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public People createPeople(@Valid @RequestBody People people) {
        return peopleRepository.save(people);
    }
}
