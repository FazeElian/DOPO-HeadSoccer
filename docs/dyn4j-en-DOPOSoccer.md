# dyn4j en DOPOSoccer

Nov 26, 2026 · @Oscar

## Qué es dyn4j y dónde se usa

dyn4j 5.0.2 es un motor de física 2D en Java: calcula gravedad, choques, rebotes y fricción entre cuerpos. En DOPOSoccer **solo una clase lo importa: `PhysicsEngine`**, que hace de adaptador (patrón Adapter) entre el dominio y la librería.

La idea es que el dominio nunca vea dyn4j:

- El dominio trabaja con `Entity` (jugadores, balón, balones falsos) en **píxeles**, y esas entidades son la fuente de verdad.
- En cada `step`, `PhysicsEngine` copia las entidades a cuerpos de dyn4j, deja que la librería avance el mundo y copia el resultado de vuelta a las entidades.
- Si mañana cambian de librería, solo se reescribe `PhysicsEngine`.

El ciclo de `PhysicsEngine.step(match, dt)` es siempre el mismo:

1. Si no hay mundo (`world == null`), lo crea con `createWorld()`.
2. `syncToBodies(match)`: entidades → cuerpos (crea los que faltan, borra los que sobran, copia posición, velocidad y gravedad).
3. `contacts.clear()` y luego `world.step(1, dt)`: dyn4j avanza la simulación y anota los choques nuevos.
4. `syncFromBodies()`: cuerpos → entidades (posición, velocidad y si el jugador está en el suelo).
5. `onContact(...)` por cada choque guardado: avisa al `Match` si el balón tocó a un jugador o un borde.

## Unidades: metros y píxeles

dyn4j trabaja en **metros, segundos y kilogramos**; la pantalla y el dominio, en **píxeles**. Por eso cada valor que cruza entre los dos se divide o se multiplica por `PIXELS_PER_METER = 100` (100 px = 1 m).

| Dirección | Operación | Ejemplo en el código |
| --- | --- | --- |
| Dominio → dyn4j | dividir por 100 | `Geometry.createCircle(33 / PIXELS_PER_METER)`: radio de 33 px = 0,33 m |
| dyn4j → dominio | multiplicar por 100 | `center.x * PIXELS_PER_METER`: de metros a píxeles |

- Se usa 100 porque dyn4j está afinado para objetos de 0,1 a 10 m. Si le pasaran píxeles directo, un jugador de 105 px de ancho sería un objeto de 105 m y la física se vería lenta y rara.
- `GRAVITY = 9.8` m/s², la de la Tierra, positiva hacia abajo: en la pantalla la *y* crece hacia abajo, así que la gravedad también.
- dyn4j ubica los cuerpos por su **centro**; el dominio guarda la esquina superior izquierda. Por eso existen `centerX(e)` y `centerY(e)` (esquina + mitad del ancho o alto) y, al volver, se resta `e.getWidth() / 2`.

## World: el mundo de la simulación

`World<Body>` (paquete `org.dyn4j.world`) es el contenedor de todos los cuerpos: les aplica la gravedad, detecta los choques y los resuelve. En el proyecto hay un solo mundo por partido, en el atributo `world` de `PhysicsEngine`, y se crea en `createWorld()` la primera vez que se llama `step`.

| Método de dyn4j | Qué hace | Dónde lo usan |
| --- | --- | --- |
| `new World<>()` | Crea un mundo vacío. El `<Body>` dice que sus cuerpos son de la clase `Body`. | `createWorld()` |
| `setGravity(x, y)` | Fija la gravedad en m/s². | `world.setGravity(0, GRAVITY)`: 0 en x, 9,8 hacia abajo |
| `getSettings().setAtRestDetectionEnabled(false)` | Apaga el "reposo": por defecto dyn4j duerme los cuerpos quietos para ahorrar cálculo y deja de moverlos. | `createWorld()`: así un balón quieto se mueve apenas lo patean |
| `addBody(body)` | Mete un cuerpo en la simulación. | `register`, `addStatic`, `addCrossbar`, `addGoalArea` |
| `removeBody(body)` | Saca un cuerpo. | `syncToBodies`, cuando desaparece un balón falso |
| `step(1, dt)` | Avanza la simulación. El `1` es el número de pasos y `dt` los segundos de ese paso. | `step(match, dt)` |
| `addContactListener(listener)` | Registra a quién avisar cuando dos cuerpos chocan. | `createWorld()` (ver la sección de contactos) |

El mundo **no es serializable**. Si guardan una partida, `world` y los mapas de cuerpos deben ser `transient`: al abrirla, `world` llega en `null` y el siguiente `step` lo reconstruye con `createWorld()`.

## Body, BodyFixture y MassType

Un `Body` (paquete `org.dyn4j.dynamics`) es un objeto físico: tiene posición, velocidad y masa. Su forma la dan sus **fixtures**: cada `BodyFixture` es una figura (círculo, rectángulo, cápsula…) con sus propiedades de material. Un cuerpo puede tener varias fixtures pegadas, como el jugador.

### Métodos de Body

| Método | Qué hace |
| --- | --- |
| `new Body()` | Crea un cuerpo vacío, sin forma. |
| `addFixture(forma)` | Le pega una figura con valores por defecto y devuelve la `BodyFixture`. |
| `addFixture(forma, densidad, fricción, restitución)` | Igual, pero fijando el material: **densidad** (con el área da la masa), **fricción** (0 = hielo, 1 = muy áspero) y **restitución** (cuánto rebota: 0 = nada, 1 = rebote perfecto). |
| `setMass(MassType)` | Calcula la masa a partir de las fixtures, según el tipo (tabla de abajo). |
| `translate(x, y)` | Mueve el cuerpo. Se usa una sola vez, para ubicar los cuerpos quietos. |
| `setBullet(true)` | Activa la detección continua: revisa el camino entre un paso y otro para que un objeto muy rápido no atraviese una pared. |
| `setLinearDamping(0.05)` | Frena un poco el movimiento con el tiempo, como la resistencia del aire. |

### Métodos de BodyFixture

| Método | Qué hace |
| --- | --- |
| `setFriction(valor)` | Cambia la fricción de esa figura. |
| `setFilter(CategoryFilter)` | Dice con qué choca esa figura (ver la sección de CategoryFilter). |

### MassType (paquete `org.dyn4j.geometry`)

| Valor | Significado | Quién lo usa |
| --- | --- | --- |
| `NORMAL` | Masa calculada; se mueve y rota libremente. | Balón y balones falsos |
| `FIXED_ANGULAR_VELOCITY` | Se mueve, pero **no rota**. | Jugadores: así no se caen ni dan vueltas al chocar |
| `INFINITE` | Masa infinita: nada lo mueve. | Suelo, paredes, techo, travesaños y zonas de gol |

### Los cuerpos de DOPOSoccer

| Cuerpo | Método que lo crea | Forma | Material (densidad, fricción, rebote) |
| --- | --- | --- | --- |
| Jugador | `addPlayer` | Cabeza: círculo de 33 px de radio, subido 8 px. Pies: cápsula de 45 × 20 px, 35 px abajo y 7 px hacia el lado al que mira. | 1.0, 0.0, 0.0: no se pega a las paredes ni rebota |
| Balón | `addBall` → `circle` | Círculo del tamaño del balón | 0.5, 0.3, rebote 0.7 con `Ball.RESTITUTION`; además `setBullet(true)` y `setLinearDamping(0.05)` |
| Balón falso | `addFakeBall` → `circle` | Igual que el balón | Igual que el balón |
| Suelo, paredes y techo | `addStatic` | Rectángulos de 200 px de grosor por fuera del campo | Fricción 0.6 |
| Travesaño | `addCrossbar` | Triángulo: 25 px de alto contra la pared y 0 en el poste, para que el balón ruede hacia la cancha | Fricción 0.5 |
| Zona de gol | `addGoalArea` | Rectángulo del tamaño del arco | Fricción 0: el jugador se desliza en vez de quedarse pegado |

`register(e, body)` guarda cada cuerpo móvil en el mapa `bodies` (entidad → cuerpo) y lo mete al mundo. Los cuerpos quietos no van en `bodies`, porque no hay entidad que sincronizar: se agregan directo con `world.addBody`.

## Geometry y Convex: las formas

`Geometry` (paquete `org.dyn4j.geometry`) es una fábrica de formas: tiene métodos estáticos que devuelven un `Convex`, la interfaz de cualquier figura convexa (sin entrantes). Todas las medidas van en **metros** y la forma queda centrada en (0, 0) del cuerpo.

| Método | Qué crea | Dónde lo usan |
| --- | --- | --- |
| `Geometry.createCircle(radio)` | Círculo | Cabeza del jugador; balón y balones falsos (`circle`) |
| `Geometry.createRectangle(ancho, alto)` | Rectángulo | Suelo, paredes y techo (`addStatic`); zona de gol (`addGoalArea`) |
| `Geometry.createCapsule(ancho, alto)` | Cápsula: rectángulo con los extremos redondos | Pies del jugador; al ser redondos, no se traban en las esquinas |
| `Geometry.createTriangle(p1, p2, p3)` | Triángulo con 3 puntos `Vector2` | Travesaño (`addCrossbar`) |
| `Geometry.cleanse(puntos...)` | Ordena los puntos en sentido antihorario y quita los repetidos; dyn4j lo exige para crear polígonos | `addCrossbar`, antes de `createTriangle` |
| `forma.translate(x, y)` | Corre la figura dentro de su cuerpo | Sube la cabeza 8 px y baja los pies 35 px en `addPlayer` |

Ojo con la diferencia: `forma.translate` mueve la figura **dentro** del cuerpo (por eso la cabeza y los pies quedan en sitios distintos del mismo jugador), mientras que `body.translate` mueve el cuerpo entero en el mundo.

## Transform y Vector2: posición y velocidad

Cada `Body` tiene un `Transform` (su posición y ángulo en el mundo) y una velocidad lineal. Los dos se leen y escriben con `Vector2` (paquete `org.dyn4j.geometry`), un vector 2D con campos públicos `x` y `y`.

| Método | Qué hace | Dónde lo usan |
| --- | --- | --- |
| `body.getTransform().setTranslation(x, y)` | Pone el centro del cuerpo en (x, y) metros. | `syncToBodies`: copia la posición de la entidad al cuerpo |
| `body.getTransform().getTranslation()` | Devuelve el centro como `Vector2`. | `syncFromBodies`: lee la posición después del paso |
| `body.setLinearVelocity(vx, vy)` | Fija la velocidad en m/s. | `syncToBodies`: correr, saltar, patear, saque |
| `body.getLinearVelocity()` | Devuelve la velocidad como `Vector2`. | `syncFromBodies` |
| `body.setGravityScale(escala)` | Multiplica la gravedad solo para ese cuerpo: 1 normal, 0 sin gravedad. | `syncToBodies`, con `e.getGravityScale()`: un tiro de poder recto pone 0 para ir derecho, el balón normal usa 0.7 (cae un poco más lento) y los jugadores 1 |
| `new Vector2(x, y)` | Crea un punto o vector. | Los 3 vértices del travesaño |

**Por qué se copia en los dos sentidos en cada paso:** el dominio cambia posiciones y velocidades por su cuenta (un salto pone `vy` negativa, el saque pone el balón en el centro). Entonces, antes de cada `world.step`, `syncToBodies` le pasa a dyn4j lo que el dominio decidió; después del paso, `syncFromBodies` le devuelve al dominio lo que calculó dyn4j (caídas, rebotes, empujones). Al final, `syncFromBodies` también marca `setOnGround` en cada jugador si su borde inferior está a 2 px o menos del suelo.

## CategoryFilter: quién choca con quién

`CategoryFilter` (paquete `org.dyn4j.collision`) decide qué figuras chocan entre sí. Se crea con `new CategoryFilter(categoría, máscara)` y se le pone a cada fixture con `setFilter`.

- **Categoría:** qué es la figura. Cada una es una potencia de 2, así cada categoría ocupa un bit distinto.
- **Máscara:** con qué categorías quiere chocar. Se arma uniendo categorías con `|` (O a nivel de bits): `STATIC | PLAYER` = 1 + 2 = 3.
- **Regla de dyn4j:** A y B chocan solo si la máscara de A incluye la categoría de B **y** la máscara de B incluye la categoría de A. Si alguno no quiere, se atraviesan.

| Constante | Valor (bit) | Qué es | Máscara (con qué quiere chocar) |
| --- | --- | --- | --- |
| `STATIC` | 1 | Suelo, paredes, techo, travesaños | `PLAYER \| BALL \| FAKE` |
| `PLAYER` | 2 | Jugadores | `STATIC \| PLAYER \| BALL \| GOAL_AREA` |
| `BALL` | 4 | Balón real | `STATIC \| PLAYER` |
| `FAKE` | 8 | Balones falsos del tiro Ilusión | `STATIC` |
| `GOAL_AREA` | 16 | Caja sobre cada arco | `PLAYER` |

Resultado, aplicando la regla en los dos sentidos:

|  | Campo | Jugador | Balón | Balón falso | Zona de gol |
| --- | --- | --- | --- | --- | --- |
| **Jugador** | Sí | Sí | Sí | No | Sí |
| **Balón** | Sí | Sí | — | No | No |
| **Balón falso** | Sí | No | No | No | No |

Así se logran tres reglas del juego sin escribir ningún `if`:

- Los balones falsos rebotan en la cancha pero **atraviesan** a los jugadores: por eso engañan.
- La zona de gol **frena al jugador** para que no se meta al arco, pero el balón pasa y puede ser gol.
- Los travesaños son `STATIC`, así que todos rebotan en ellos.

## Contactos: cómo se enteran de los choques

dyn4j avisa de los choques con un **listener** (patrón Observer). En `createWorld()` se registra uno con `world.addContactListener(...)`, que guarda cada choque nuevo; después del paso, `onContact` decide qué significa para el juego.

| Clase / método | Paquete | Qué es |
| --- | --- | --- |
| `ContactListenerAdapter<Body>` | `org.dyn4j.world.listener` | Clase con todos los métodos del listener vacíos: se sobrescribe solo el que interesa. |
| `begin(collision, contact)` | — | dyn4j lo llama cuando dos figuras **empiezan** a tocarse. Hay otros (`persist` mientras siguen tocando, `end` cuando se separan) que el proyecto no usa. |
| `ContactCollisionData<Body>` | `org.dyn4j.world` | Datos del choque: `getBody1()` y `getBody2()` dan los dos cuerpos. |
| `Contact` | `org.dyn4j.dynamics.contact` | Detalle del punto de contacto (posición, profundidad). El proyecto no lo usa, pero el método lo recibe. |

El listener del proyecto es una clase anónima:

```java
world.addContactListener(new ContactListenerAdapter<Body>() {
    @Override
    public void begin(ContactCollisionData<Body> collision, Contact contact) {
        contacts.add(List.of(collision.getBody1(), collision.getBody2()));
    }
});
```

**Cómo se guardan y se procesan:**

1. Antes de cada `world.step`, `step` vacía `contacts`.
2. Durante el paso, `begin` agrega cada pareja de cuerpos como una lista de 2 a `contacts`, un `LinkedHashSet<List<Body>>`. Es un *set* porque el balón puede tocar la cabeza y el pie del mismo jugador en el mismo paso y la pareja se guarda una sola vez; es *Linked* para respetar el orden en que pasaron.
3. No se actúa dentro de `begin` porque dyn4j está a mitad del cálculo. Después del paso, `step` recorre una copia de `contacts` y llama `onContact(a, b, match)`.
4. `onContact` solo atiende choques del balón real (`ballBody`):
   - Si el otro cuerpo está en el mapa `players` → `match.onBallTouch(jugador)`: el `Match` decide si sale o pega un tiro de poder.
   - Si está en `borders` (paredes, techo, travesaños) → `match.onBallHitsBorder()`, que termina el tiro de poder. Si además está en `crossbars`, agrega el evento `POST` para el sonido del palo.
   - Cualquier otro choque (jugador con suelo, balones falsos) se ignora.

Los goles no salen de aquí: el `Match` los revisa comparando la posición del balón con el área de cada `Goal`.

## La física en las extensiones

Casi cualquier extensión física se hace con una de estas seis palancas, sin tocar dyn4j directamente fuera de `PhysicsEngine`. La regla de oro: **el dominio cambia sus entidades (`Entity`) y `PhysicsEngine` lo traduce en el siguiente `step`**.

| Quieres que… | Palanca | Dónde se toca | Ejemplo que practicamos |
| --- | --- | --- | --- |
| Algo salga disparado de golpe | Cambiar la velocidad **una vez** con `setVelocity` | En `launch` de un poder o en `onApply` de un efecto | Tiros de poder, Empuje, Inconsciente |
| Algo se empuje **poco a poco** | Sumar a la velocidad en **cada tick**: `vx += fuerza × dt` | `Match.update`, antes de `physics.step` | Viento, temblor del terremoto |
| Algo aparezca en otro sitio | Cambiar la posición con `setPosition` | En `launch` de un poder | Portal (Egipto) |
| Algo caiga más rápido, más lento o nada | Cambiar `getGravityScale()` de la entidad | La entidad (`Ball`, `Obstacle`…) | Tiros rectos (0), skin pesada o liviana |
| Algo atraviese o choque con otra cosa | Cambiar el `CategoryFilter` (categoría y máscara) | `PhysicsEngine` | Balones falsos, tiro Fantasma |
| Aparezca un objeto nuevo en la cancha | Entidad nueva con `addTo` + cuerpo en `PhysicsEngine` | `Entity`, `Match`, `PhysicsEngine` | Muro defensivo, obstáculos |

### 1. Velocidad de golpe: `setVelocity` una vez

En el dominio, `Entity.setVelocity(vx, vy)` cambia la velocidad en px/s. En el siguiente `step`, `syncToBodies` la copia al cuerpo con `body.setLinearVelocity(vx / 100, vy / 100)` y desde ahí dyn4j la sigue cambiando: gravedad, choques, rebotes.

- Recuerda: `vx` positiva es a la derecha y `vy` positiva es **hacia abajo**.
- Para lanzar hacia el arco rival se multiplica por `shooter.getDirection()` (1 o −1).
- Para lanzar hacia un punto exacto (el Fantasma al centro del arco): se calcula la flecha `(dx, dy)` = destino − origen, se divide por su largo (`Math.hypot(dx, dy)`) y se multiplica por la velocidad que quieras.

### 2. Empuje continuo: sumar velocidad en cada tick

Una fuerza que dura (viento, imán, temblor) es una **aceleración**: en cada tick se suma `fuerza × dt` a la velocidad. Con `fuerza = 600` px/s², cada segundo el objeto gana 600 px/s hacia ese lado, sin importar cuántos ticks haya.

```java
ball.setVelocity(ball.getVx() + wind * dt, ball.getVy());
```

Va en `Match.update` **antes** de `physics.step`, para que `syncToBodies` le pase a dyn4j la velocidad ya empujada. Ojo con los jugadores: `Player.act` vuelve a poner su `vx` en cada tick (correr o `stop()`), así que un empuje sobre un jugador se aplica **después** de `act`. Si es una oscilación (temblor), resta el empuje anterior antes de sumar el nuevo, para que no se acumule.

### 3. Teletransporte: `setPosition`

`Entity.setPosition(x, y)` mueve la esquina superior izquierda en píxeles. Funciona porque **las entidades son la fuente de verdad**: en el siguiente `step`, `syncToBodies` pone el cuerpo en esa posición con `getTransform().setTranslation(...)`. No hace falta tocar dyn4j.

Si lo pones encima de otro cuerpo (un jugador, una pared), dyn4j los separa de un empujón en el siguiente paso, y eso cuenta como un choque.

### 4. Gravedad por entidad: `getGravityScale()`

Cada `step`, `syncToBodies` hace `body.setGravityScale(e.getGravityScale())`. dyn4j multiplica la gravedad del mundo (9.8 m/s²) por ese número solo para ese cuerpo.

| Valor | Efecto | Quién lo usa hoy |
| --- | --- | --- |
| 1 | Gravedad normal | Jugadores |
| 0.7 | Cae un poco más lento | Balón normal |
| 0 | No cae: va recto | Balón con un tiro recto, obstáculos |
| Más de 1 | Cae más rápido | Una skin pesada (`0.7 × gravityFactor`) |

Como se pregunta en cada paso, se puede cambiar en cualquier momento sobrescribiendo el método en la entidad.

### 5. Quién choca con quién en tiempo real: el filtro

El `CategoryFilter` de una forma se pone al crearla, pero se puede cambiar en cualquier momento. Para el balón:

```java
long ballMask = match.getBall().passesThroughPlayers() ? STATIC : STATIC | PLAYER;
ballBody.getFixture(0).setFilter(new CategoryFilter(BALL, ballMask));
```

- `getFixture(0)` es la primera (y única) forma del balón; el jugador tiene dos: cabeza (0) y pies (1).
- Se revisa en cada `syncToBodies`, así que se apaga solo cuando el balón pierde la carga.
- Si una forma no choca, dyn4j tampoco llama al listener de contactos: por eso el Fantasma nunca "toca" al rival y el efecto se aplica en `launch`.

### 6. Un objeto nuevo en la cancha

Es el mismo camino que siguen los balones falsos:

1. **Entidad nueva** que extiende `Entity`, con su tiempo de vida si es temporal y `addTo(engine)` llamando a un método nuevo de `PhysicsEngine` (double dispatch).
2. **Lista en `Match`**, con `addXxx`, `getXxx` y el descuento del tiempo en `update`.
3. **`entitiesOf`** la incluye. Desde ahí `syncToBodies` crea el cuerpo cuando aparece y lo **borra del mundo** (`world.removeBody`) cuando ya no está en la lista.
4. **El cuerpo** en `PhysicsEngine`:

```java
Body body = new Body();
BodyFixture fixture = body.addFixture(Geometry.createRectangle(ancho / PIXELS_PER_METER, alto / PIXELS_PER_METER));
fixture.setFilter(new CategoryFilter(STATIC, PLAYER | BALL | FAKE));   // choca con todos, como una pared
body.setMass(MassType.INFINITE);                                       // nada lo mueve
register(entidad, body);                                               // a `bodies` y al mundo
borders.add(body);                                                     // opcional: apaga tiros de poder
```

5. **Getter en la fachada** que devuelva `{x, y, ancho, alto}` y **dibujo en `MatchView`**.

Si el objeto debe moverse (por ejemplo, una plataforma que va y viene), usa `MassType.NORMAL` o `INFINITE` y cambia su velocidad desde el dominio en cada tick; si debe ser atravesado por los balones pero no por los jugadores, ponle una categoría propia (por ejemplo `32`) y ajústale las máscaras, como la zona del arco (`GOAL_AREA`).

### 7. Enterarse de un choque

Los choques llegan por `PhysicsEngine.onContact`, que hoy solo atiende al **balón real** contra jugadores o bordes. Para reaccionar a otro choque (por ejemplo, un jugador que toca un power-up):

- Guarda los cuerpos de ese tipo en un mapa, como `players` (cuerpo → entidad).
- En `onContact`, si uno de los dos cuerpos está en ese mapa, avisa al `Match` con un método nuevo (`match.onPowerUpTouched(...)`).
- Para que el choque se detecte pero **no frene** al jugador, el power-up tiene que chocar con `PLAYER` en el filtro; si lo quieres atravesable, marca la forma como sensor con `fixture.setSensor(true)`: dyn4j avisa del contacto pero no empuja.

## Resumen: todo lo de dyn4j que usa el proyecto

Son 11 clases de dyn4j importadas (más `Transform`, que se usa sin importarla), todas solo en `PhysicsEngine`.

| Clase | Paquete | Métodos que usan | Para qué |
| --- | --- | --- | --- |
| `World<Body>` | `org.dyn4j.world` | `new World<>()`, `setGravity`, `getSettings().setAtRestDetectionEnabled`, `addBody`, `removeBody`, `step`, `addContactListener` | El mundo que simula todo |
| `Body` | `org.dyn4j.dynamics` | `new Body()`, `addFixture`, `setMass`, `translate`, `setBullet`, `setLinearDamping`, `getTransform`, `setLinearVelocity`, `getLinearVelocity`, `setGravityScale` | Cada objeto físico |
| `BodyFixture` | `org.dyn4j.dynamics` | `setFriction`, `setFilter` | La forma y el material de un cuerpo |
| `MassType` | `org.dyn4j.geometry` | `NORMAL`, `FIXED_ANGULAR_VELOCITY`, `INFINITE` | Cómo se mueve: libre, sin rotar o quieto |
| `Geometry` | `org.dyn4j.geometry` | `createCircle`, `createRectangle`, `createCapsule`, `createTriangle`, `cleanse` | Fabrica las formas |
| `Convex` | `org.dyn4j.geometry` | `translate` | Una forma, para correrla dentro del cuerpo |
| `Vector2` | `org.dyn4j.geometry` | `new Vector2(x, y)`, campos `x` y `y` | Puntos, posiciones y velocidades |
| `Transform` | `org.dyn4j.geometry` | `setTranslation`, `getTranslation` | Posición del cuerpo (no se importa: se usa a través de `getTransform()`) |
| `CategoryFilter` | `org.dyn4j.collision` | `new CategoryFilter(categoría, máscara)` | Quién choca con quién |
| `ContactListenerAdapter<Body>` | `org.dyn4j.world.listener` | `begin` (sobrescrito) | Enterarse de los choques |
| `ContactCollisionData<Body>` y `Contact` | `org.dyn4j.world` y `org.dyn4j.dynamics.contact` | `getBody1`, `getBody2` | Los datos de cada choque |

Para leer la documentación oficial en inglés de cualquiera de estas clases, busca su paquete en el javadoc de dyn4j 5.0.2 (`dyn4j-5.0.2-javadoc.jar` o https://javadoc.io/doc/org.dyn4j/dyn4j/5.0.2).
