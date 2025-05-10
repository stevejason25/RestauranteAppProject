import java.util.ArrayList;
import java.util.List;

// --- PATRÓN OBSERVER: Interfaces ---

/**
 * Interfaz para el Sujeto (Observable) en el patrón Observer.
 * Define métodos para registrar, eliminar y notificar observadores.
 */
interface SujetoPedido {
    void registrarObservador(ObservadorPedido observador);
    void eliminarObservador(ObservadorPedido observador);
    void notificarObservadores();
}

/**
 * Interfaz para el Observador en el patrón Observer.
 * Define el método que será llamado cuando el sujeto cambie.
 */
interface ObservadorPedido {
    void actualizar(Pedido pedido);
}

// --- PATRÓN DECORATOR: Componente y Decorador Base ---

/**
 * Interfaz Componente (Plato) para el patrón Decorator.
 * Define las operaciones que pueden ser alteradas por los decoradores.
 */
interface Plato {
    String getNombre();
    double getPrecio();
    String getDescripcion(); // Para mostrar el plato con sus extras
}

/**
 * Clase abstracta Decorador (ExtraPlatoDecorator) para el patrón Decorator.
 * Mantiene una referencia al objeto Componente (Plato) y define una interfaz
 * conforme a la interfaz del Componente.
 */
abstract class ExtraPlatoDecorator implements Plato {
    protected Plato platoDecorado;

    public ExtraPlatoDecorator(Plato platoDecorado) {
        this.platoDecorado = platoDecorado;
    }

    @Override
    public String getNombre() {
        return platoDecorado.getNombre();
    }

    @Override
    public double getPrecio() {
        return platoDecorado.getPrecio();
    }

    @Override
    public String getDescripcion() {
        return platoDecorado.getDescripcion();
    }
}

// --- PATRÓN FACTORY METHOD: Producto y Fábrica ---

/**
 * Clase base para los productos concretos (PlatoBase).
 * Implementa la interfaz Plato y sirve como base para los diferentes tipos de platos.
 */
class PlatoBase implements Plato {
    protected String nombre;
    protected double precio;
    protected String tipo; // Para identificar el tipo de plato

    public PlatoBase(String nombre, double precio, String tipo) {
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    @Override
    public String getDescripcion() {
        return tipo + ": " + nombre;
    }
}

// Productos Concretos (Tipos de Plato)
class PlatoPrincipal extends PlatoBase {
    public PlatoPrincipal(String nombre, double precio) {
        super(nombre, precio, "Plato Principal");
    }
}

class Bebida extends PlatoBase {
    public Bebida(String nombre, double precio) {
        super(nombre, precio, "Bebida");
    }
}

class Postre extends PlatoBase {
    public Postre(String nombre, double precio) {
        super(nombre, precio, "Postre");
    }
}

/**
 * Fábrica Concreta (PlatoFactory) para el patrón Factory Method.
 * Implementa el método para crear objetos Plato.
 */
class PlatoFactory {
    /**
     * Método de fábrica para crear diferentes tipos de platos.
     * @param tipo El tipo de plato a crear ("principal", "bebida", "postre").
     * @param nombre El nombre del plato.
     * @param precio El precio del plato.
     * @return Una instancia de Plato.
     * @throws IllegalArgumentException si el tipo de plato no es válido.
     */
    public Plato crearPlato(String tipo, String nombre, double precio) {
        if ("principal".equalsIgnoreCase(tipo)) {
            return new PlatoPrincipal(nombre, precio);
        } else if ("bebida".equalsIgnoreCase(tipo)) {
            return new Bebida(nombre, precio);
        } else if ("postre".equalsIgnoreCase(tipo)) {
            return new Postre(nombre, precio);
        }
        throw new IllegalArgumentException("Tipo de plato desconocido: " + tipo);
    }
}

// --- DECORADORES CONCRETOS ---

/**
 * Decorador Concreto: ConExtraQueso.
 * Añade queso extra a un plato.
 */
class ConExtraQueso extends ExtraPlatoDecorator {
    private double precioExtraQueso = 1.50;

    public ConExtraQueso(Plato platoDecorado) {
        super(platoDecorado);
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + precioExtraQueso;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " (con extra queso)";
    }
}

/**
 * Decorador Concreto: ConExtraSalsa.
 * Añade salsa extra a un plato.
 */
class ConExtraSalsa extends ExtraPlatoDecorator {
    private double precioExtraSalsa = 0.75;
    private String tipoSalsa;

    public ConExtraSalsa(Plato platoDecorado, String tipoSalsa) {
        super(platoDecorado);
        this.tipoSalsa = tipoSalsa;
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + precioExtraSalsa;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " (con extra salsa " + tipoSalsa + ")";
    }
}

// --- CLASES DEL CASO DE USO: Pedido y Cliente ---

/**
 * Enum para los estados del pedido.
 */
enum EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO_PARA_ENTREGA,
    ENTREGADO,
    CANCELADO
}

/**
 * Clase Pedido (Sujeto Concreto en Observer).
 * Contiene la lista de platos, el estado del pedido y gestiona los observadores.
 */
class Pedido implements SujetoPedido {
    private int idPedido;
    private List<Plato> items;
    private EstadoPedido estado;
    private List<ObservadorPedido> observadores;
    private static int contadorPedidos = 0;

    public Pedido() {
        this.idPedido = ++contadorPedidos;
        this.items = new ArrayList<>();
        this.estado = EstadoPedido.RECIBIDO; // Estado inicial
        this.observadores = new ArrayList<>();
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void agregarItem(Plato plato) {
        items.add(plato);
        System.out.println("'" + plato.getDescripcion() + "' agregado al pedido " + idPedido + ".");
    }

    public double getTotal() {
        double total = 0;
        for (Plato item : items) {
            total += item.getPrecio();
        }
        return total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    /**
     * Cambia el estado del pedido y notifica a los observadores.
     * @param nuevoEstado El nuevo estado del pedido.
     */
    public void setEstado(EstadoPedido nuevoEstado) {
        if (this.estado != nuevoEstado) {
            System.out.println("\n--- CAMBIO DE ESTADO PEDIDO " + idPedido + " ---");
            System.out.println("Estado anterior: " + this.estado);
            this.estado = nuevoEstado;
            System.out.println("Nuevo estado: " + this.estado);
            notificarObservadores(); // Notificar a los observadores sobre el cambio
            System.out.println("---------------------------------");
        }
    }

    @Override
    public void registrarObservador(ObservadorPedido observador) {
        observadores.add(observador);
    }

    @Override
    public void eliminarObservador(ObservadorPedido observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores() {
        for (ObservadorPedido observador : observadores) {
            observador.actualizar(this);
        }
    }

    public void mostrarDetalle() {
        System.out.println("\n--- Detalle del Pedido " + idPedido + " ---");
        System.out.println("Estado: " + estado);
        if (items.isEmpty()) {
            System.out.println("El pedido está vacío.");
        } else {
            System.out.println("Items:");
            for (Plato item : items) {
                System.out.printf("  - %-40s $%.2f%n", item.getDescripcion(), item.getPrecio());
            }
        }
        System.out.printf("Total del Pedido: $%.2f%n", getTotal());
        System.out.println("--------------------------");
    }
}

/**
 * Clase Cliente (Observador Concreto en Observer).
 * Recibe notificaciones sobre los cambios en el estado del pedido.
 */
class Cliente implements ObservadorPedido {
    private String nombre;

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Método que se ejecuta cuando el pedido (sujeto) notifica un cambio.
     * @param pedido El pedido que ha cambiado de estado.
     */
    @Override
    public void actualizar(Pedido pedido) {
        System.out.println("Notificación para Cliente '" + nombre + "': El pedido #" + pedido.getIdPedido() +
                            " ha cambiado su estado a: " + pedido.getEstado());
    }
}

// --- CLASE PRINCIPAL PARA DEMOSTRACIÓN EN CONSOLA ---
// --- CLASE PRINCIPAL PARA DEMOSTRACIÓN EN CONSOLA ---
public class RestauranteApp {
    public static void main(String[] args) {
        System.out.println("Bienvenido al Sistema de Gestión de Pedidos del Restaurante!");
        System.out.println("=========================================================\n");

        // 1. Uso del Patrón Factory Method para crear platos
        System.out.println("--- DEMOSTRACIÓN: PATRÓN FACTORY METHOD ---");
        PlatoFactory fabricaDePlatos = new PlatoFactory();
        Plato pizza = fabricaDePlatos.crearPlato("principal", "Pizza Margherita", 12.50);
        Plato cola = fabricaDePlatos.crearPlato("bebida", "Refresco de Cola", 2.00);
        Plato helado = fabricaDePlatos.crearPlato("postre", "Helado de Chocolate", 4.75);

        System.out.println("Plato creado: " + pizza.getDescripcion() + " - $" + pizza.getPrecio());
        System.out.println("Plato creado: " + cola.getDescripcion() + " - $" + cola.getPrecio());
        System.out.println("Plato creado: " + helado.getDescripcion() + " - $" + helado.getPrecio());
        System.out.println("-----------------------------------------\n");


        // 2. Uso del Patrón Decorator para añadir extras a los platos
        System.out.println("--- DEMOSTRACIÓN: PATRÓN DECORATOR ---");
        // Decorar la pizza con extra queso
        Plato pizzaConQueso = new ConExtraQueso(pizza);
        System.out.println("Plato decorado: " + pizzaConQueso.getDescripcion() + " - $" + pizzaConQueso.getPrecio());

        // Decorar la pizza con queso y luego con extra salsa picante
        Plato pizzaFullExtras = new ConExtraSalsa(pizzaConQueso, "Picante");
        System.out.println("Plato decorado: " + pizzaFullExtras.getDescripcion() + " - $" + pizzaFullExtras.getPrecio());

        // Decorar un plato base directamente
        Plato hamburguesa = fabricaDePlatos.crearPlato("principal", "Hamburguesa Clásica", 8.00);
        Plato hamburguesaConQueso = new ConExtraQueso(hamburguesa);
        System.out.println("Plato decorado: " + hamburguesaConQueso.getDescripcion() + " - $" + hamburguesaConQueso.getPrecio());
        System.out.println("------------------------------------\n");


        // 3. Creación de un Pedido y adición de items
        System.out.println("--- CREACIÓN DE PEDIDO Y ADICIÓN DE ITEMS ---");
        Pedido pedido1 = new Pedido();
        System.out.println("Creado Pedido #" + pedido1.getIdPedido());

        pedido1.agregarItem(pizzaFullExtras); // Pizza con queso y salsa
        pedido1.agregarItem(cola);             // Refresco normal
        pedido1.agregarItem(new ConExtraSalsa(fabricaDePlatos.crearPlato("postre", "Flan Casero", 3.50), "Caramelo")); // Postre decorado

        pedido1.mostrarDetalle();
        System.out.println("-------------------------------------------\n");

        // 4. Uso del Patrón Observer para notificar cambios de estado del pedido
        System.out.println("--- DEMOSTRACIÓN: PATRÓN OBSERVER ---");
        Cliente clienteAna = new Cliente("Ana");
        Cliente clienteJuan = new Cliente("Juan");

        // Registrar clientes como observadores del pedido1
        pedido1.registrarObservador(clienteAna);
        pedido1.registrarObservador(clienteJuan);
        System.out.println("Cliente '" + clienteAna.getNombre() + "' y '" + clienteJuan.getNombre() + "' están observando el Pedido #" + pedido1.getIdPedido() + ".");

        // Cambiar estado del pedido1 (esto notificará a los observadores registrados)
        pedido1.setEstado(EstadoPedido.EN_PREPARACION);
        pedido1.setEstado(EstadoPedido.LISTO_PARA_ENTREGA);

        // Un cliente deja de observar el pedido1
        pedido1.eliminarObservador(clienteJuan);
        System.out.println("\nCliente '" + clienteJuan.getNombre() + "' ya no observa el Pedido #" + pedido1.getIdPedido() + ".");

        // Último cambio de estado para pedido1 (solo Ana recibirá esta notificación)
        pedido1.setEstado(EstadoPedido.ENTREGADO);

        // Crear otro pedido para demostrar que los observadores son por pedido
        System.out.println("\n--- OTRO PEDIDO Y OBSERVADOR ---");
        Pedido pedido2 = new Pedido();
        Cliente clienteLuis = new Cliente("Luis");
        pedido2.registrarObservador(clienteLuis);
        pedido2.agregarItem(fabricaDePlatos.crearPlato("bebida", "Jugo de Naranja", 2.50));

        // Cambiar estados del pedido2 (solo Luis será notificado)
        pedido2.setEstado(EstadoPedido.EN_PREPARACION);
        pedido2.setEstado(EstadoPedido.LISTO_PARA_ENTREGA); // <--- Estado añadido
        pedido2.setEstado(EstadoPedido.ENTREGADO);         // <--- Estado añadido

        pedido2.mostrarDetalle(); // Mostrará el detalle final del pedido 2 (estado ENTREGADO)
        System.out.println("--------------------------------\n");

        System.out.println("=========================================================");
        System.out.println("Demostración finalizada.");
    }

}