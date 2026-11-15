package com.example.a23200581solis_24100586hancco_pc02.ui.images
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.ApodEntity

@Composable
fun ImagesApp(viewModel: ImagesViewModel) {

    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route.orEmpty()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = route.startsWith("images"),
                    onClick = {
                        navController.navigate("images") {
                            popUpTo(
                                navController.graph
                                    .findStartDestination().id
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("▧") },
                    label = { Text("Imágenes") }
                )

                NavigationBarItem(
                    selected = route.startsWith("asteroids"),
                    onClick = {
                        navController.navigate("asteroids") {
                            popUpTo(
                                navController.graph
                                    .findStartDestination().id
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("☄") },
                    label = { Text("Asteroides") }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "images",
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            composable("images") {
                ImagesListScreen(
                    state = state,
                    onRefresh = viewModel::refresh,
                    onFavorites = {
                        navController.navigate("images/favorites")
                    },
                    onOpen = { image ->
                        navController.navigate(
                            "images/detail/${image.date}"
                        )
                    },
                    onFavorite = viewModel::toggleFavorite
                )
            }

            composable("images/favorites") {
                FavoritesScreen(
                    images = state.favorites,
                    onBack = {
                        navController.popBackStack()
                    },
                    onOpen = { image ->
                        navController.navigate(
                            "images/detail/${image.date}"
                        )
                    },
                    onFavorite = viewModel::toggleFavorite
                )
            }

            composable("images/detail/{date}") { entry ->

                val date = entry.arguments
                    ?.getString("date")
                    .orEmpty()

                val imageFlow = remember(date, viewModel) {
                    viewModel.observeImage(date)
                }

                val image by imageFlow.collectAsStateWithLifecycle(
                    initialValue = null
                )

                ImageDetailScreen(
                    image = image,
                    onBack = {
                        navController.popBackStack()
                    },
                    onFavorite = viewModel::toggleFavorite
                )
            }

            composable("asteroids") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Asteroides: pendiente de integrar con Rolando")
                }
            }
        }
    }
}

@Composable
private fun ImagesListScreen(
    state: ImagesUiState,
    onRefresh: () -> Unit,
    onFavorites: () -> Unit,
    onOpen: (ApodEntity) -> Unit,
    onFavorite: (ApodEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Diario espacial",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Imágenes de los últimos 7 días",
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onFavorites) {
                Text("Mis imágenes favoritas")
            }

            TextButton(
                onClick = onRefresh,
                enabled = !state.isLoading
            ) {
                Text("Actualizar")
            }
        }

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        state.error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (state.images.isEmpty()) {
            EmptyContent(
                message = if (state.isLoading) {
                    "Consultando imágenes…"
                } else {
                    "No hay imágenes disponibles para este periodo."
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            ApodList(
                images = state.images,
                onOpen = onOpen,
                onFavorite = onFavorite,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FavoritesScreen(
    images: List<ApodEntity>,
    onBack: () -> Unit,
    onOpen: (ApodEntity) -> Unit,
    onFavorite: (ApodEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Volver")
        }

        Text(
            text = "Mis imágenes favoritas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (images.isEmpty()) {
            EmptyContent(
                message = "Aún no tienes imágenes favoritas.",
                modifier = Modifier.weight(1f)
            )
        } else {
            ApodList(
                images = images,
                onOpen = onOpen,
                onFavorite = onFavorite,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ApodList(
    images: List<ApodEntity>,
    onOpen: (ApodEntity) -> Unit,
    onFavorite: (ApodEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = images,
            key = { it.date }
        ) { image ->

            Card(
                onClick = { onOpen(image) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ApodMedia(
                        image = image,
                        height = 200.dp
                    )

                    Text(
                        text = image.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = image.date,
                        style = MaterialTheme.typography.bodySmall
                    )

                    TextButton(
                        onClick = { onFavorite(image) }
                    ) {
                        Text(
                            if (image.isFavorite) {
                                "♥ Quitar de favoritos"
                            } else {
                                "♡ Agregar a favoritos"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageDetailScreen(
    image: ApodEntity?,
    onBack: () -> Unit,
    onFavorite: (ApodEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Volver")
        }

        if (image == null) {
            Text("Cargando publicación o contenido no disponible.")
        } else {
            Text(
                text = image.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = image.date,
                style = MaterialTheme.typography.bodyMedium
            )

            ApodMedia(
                image = image,
                height = 320.dp
            )

            FilledTonalButton(
                onClick = { onFavorite(image) }
            ) {
                Text(
                    if (image.isFavorite) {
                        "♥ Quitar de favoritos"
                    } else {
                        "♡ Agregar a favoritos"
                    }
                )
            }

            Text(
                text = image.explanation,
                style = MaterialTheme.typography.bodyLarge
            )

            image.credit
                ?.takeIf { it.isNotBlank() }
                ?.let { credit ->
                    Text(
                        text = "Crédito: $credit",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
        }
    }
}

@Composable
private fun ApodMedia(
    image: ApodEntity,
    height: Dp
) {
    var failed by remember(image.hdUrl) {
        mutableStateOf(false)
    }

    when {
        image.mediaType == "video" -> {
            Text(
                text = "Contenido en video",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        image.mediaType != "image" -> {
            Text("Contenido multimedia no disponible")
        }

        image.hdUrl.isNullOrBlank() || failed -> {
            Text("Imagen no disponible")
        }

        else -> {
            AsyncImage(
                model = image.hdUrl,
                contentDescription = image.title,
                contentScale = ContentScale.Fit,
                onError = { failed = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            )
        }
    }
}

@Composable
private fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(24.dp)
        )
    }
}