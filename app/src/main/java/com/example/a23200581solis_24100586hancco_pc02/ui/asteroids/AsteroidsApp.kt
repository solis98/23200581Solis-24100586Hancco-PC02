package com.example.a23200581solis_24100586hancco_pc02.ui.asteroids

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo.AsteroidEntity
import java.text.DecimalFormat

@Composable
fun AsteroidsApp(viewModel: AsteroidsViewModel) {

    val nav = rememberNavController()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = nav,
        startDestination = "neo/list",
        modifier = Modifier.fillMaxSize()
    ) {

        composable("neo/list") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Asteroides",
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { nav.navigate("neo/favorites") }
                    ) {
                        Text("Mis asteroides")
                    }

                    TextButton(
                        onClick = viewModel::refresh,
                        enabled = !state.isLoading
                    ) {
                        Text("Actualizar")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Solo potencialmente peligrosos",
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = state.onlyHazardous,
                        onCheckedChange = viewModel::setOnlyHazardous
                    )
                }

                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                state.error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                AsteroidList(
                    asteroids = state.items,
                    emptyMessage = when {
                        state.isLoading -> "Consultando asteroides…"
                        state.onlyHazardous ->
                            "No hay asteroides potencialmente peligrosos."
                        else -> "No hay asteroides disponibles."
                    },
                    onOpen = {
                        nav.navigate(
                            "neo/detail/${it.asteroidId}/${it.approachDate}"
                        )
                    },
                    onFavorite = viewModel::toggleFavorite,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        composable("neo/favorites") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextButton(onClick = { nav.popBackStack() }) {
                    Text("← Volver")
                }

                Text(
                    "Mis asteroides",
                    style = MaterialTheme.typography.headlineSmall
                )

                AsteroidList(
                    asteroids = state.favorites,
                    emptyMessage = "Aún no tienes asteroides favoritos.",
                    onOpen = {
                        nav.navigate(
                            "neo/detail/${it.asteroidId}/${it.approachDate}"
                        )
                    },
                    onFavorite = viewModel::toggleFavorite,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        composable("neo/detail/{id}/{date}") { entry ->

            val id = entry.arguments?.getString("id").orEmpty()
            val date = entry.arguments?.getString("date").orEmpty()

            val detailFlow = remember(id, date, viewModel) {
                viewModel.observeDetail(id, date)
            }

            val asteroid by detailFlow.collectAsStateWithLifecycle(
                initialValue = null
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TextButton(onClick = { nav.popBackStack() }) {
                        Text("← Volver")
                    }
                }

                val current = asteroid

                if (current == null) {
                    item {
                        Text("Cargando o contenido no disponible.")
                    }
                } else {
                    item {
                        Text(
                            current.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    item { Text("ID: ${current.asteroidId}") }

                    item {
                        Text("Aproximación: ${current.approachDate}")
                    }

                    item { HazardLabel(current) }

                    item {
                        Text(
                            "Diámetro mínimo: " +
                                    formatNeo(current.diameterMinMeters, "m")
                        )
                    }

                    item {
                        Text(
                            "Diámetro máximo: " +
                                    formatNeo(current.diameterMaxMeters, "m")
                        )
                    }

                    item {
                        Text(
                            "Velocidad: " +
                                    formatNeo(current.velocityKmh, "km/h")
                        )
                    }

                    item {
                        Text(
                            "Distancia de paso: " +
                                    formatNeo(current.distanceKm, "km")
                        )
                    }

                    item {
                        FilledTonalButton(
                            onClick = {
                                viewModel.toggleFavorite(current)
                            }
                        ) {
                            Text(favoriteLabel(current))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AsteroidList(
    asteroids: List<AsteroidEntity>,
    emptyMessage: String,
    onOpen: (AsteroidEntity) -> Unit,
    onFavorite: (AsteroidEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (asteroids.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(emptyMessage)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = asteroids,
                key = { "${it.asteroidId}_${it.approachDate}" }
            ) { asteroid ->

                Card(
                    onClick = { onOpen(asteroid) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            asteroid.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Aproximación: ${asteroid.approachDate}")

                        HazardLabel(asteroid)

                        Text(
                            "Diámetro máximo: " +
                                    formatNeo(asteroid.diameterMaxMeters, "m")
                        )

                        Text(
                            "Velocidad: " +
                                    formatNeo(asteroid.velocityKmh, "km/h")
                        )

                        Text(
                            "Distancia: " +
                                    formatNeo(asteroid.distanceKm, "km")
                        )

                        TextButton(
                            onClick = { onFavorite(asteroid) }
                        ) {
                            Text(favoriteLabel(asteroid))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HazardLabel(asteroid: AsteroidEntity) {
    if (asteroid.isPotentiallyHazardous) {
        Text(
            "⚠ Potencialmente peligroso",
            color = MaterialTheme.colorScheme.error
        )
    } else {
        Text("No clasificado como potencialmente peligroso")
    }
}

private fun favoriteLabel(asteroid: AsteroidEntity): String {
    return if (asteroid.isFavorite) {
        "♥ Quitar de favoritos"
    } else {
        "♡ Agregar a favoritos"
    }
}

private fun formatNeo(value: Double?, unit: String): String {
    if (value == null || !value.isFinite()) {
        return "No disponible"
    }

    return "${DecimalFormat("0.##").format(value)} $unit"
}