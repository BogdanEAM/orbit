/*
 Copyright (C) 2017 Electronic Arts Inc.  All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cloud.orbit.core

import cloud.orbit.core.cluster.ClusterManager
import cloud.orbit.core.logging.loggerFor
import cloud.orbit.core.runtime.PulseManager
import cloud.orbit.core.util.Pools
import cloud.orbit.core.util.VersionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.toMono

@Component
class Stage constructor(
        @Autowired private val clusterManager: ClusterManager,
        @Autowired private val pulseManager: PulseManager
){
    private val logger = loggerFor<Stage>()

    fun startup() = Unit.toMono()
            .publishOn(Pools.parallel)
            .doOnNext {
                logger.info("Orbit Version: ${VersionUtils.orbitVersion}")
                logger.info("Orbit Cluster Identity: ${clusterManager.clusterIdentity}")
                logger.info("Orbit Node Identity: ${clusterManager.localNodeInfo.nodeIdentity}")
            }
            .flatMap {
                // Start cluster
                Unit.toMono()
            }
            .doOnNext {
                pulseManager.startPulse()
            }

    fun shutdown() = Unit.toMono()
            .publishOn(Pools.parallel)
            .doOnNext {
                pulseManager.stopPulse()
            }
}