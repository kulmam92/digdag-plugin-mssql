package com.github.kulmam92.digdag.plugin.mssql;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.digdag.standards.operator.jdbc.AbstractJdbcConnectionConfig;
import io.digdag.util.DurationParam;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link MssqlConnectionConfig}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableMssqlConnectionConfig.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "MssqlConnectionConfig"})
@Immutable
public final class ImmutableMssqlConnectionConfig
    extends MssqlConnectionConfig {
  private final String host;
  private final int port;
  private final boolean ssl;
  private final String user;
  private final Optional<String> password;
  private final String database;
  private final DurationParam connectTimeout;
  private final DurationParam socketTimeout;

  private ImmutableMssqlConnectionConfig(
      String host,
      int port,
      boolean ssl,
      String user,
      Optional<String> password,
      String database,
      DurationParam connectTimeout,
      DurationParam socketTimeout) {
    this.host = host;
    this.port = port;
    this.ssl = ssl;
    this.user = user;
    this.password = password;
    this.database = database;
    this.connectTimeout = connectTimeout;
    this.socketTimeout = socketTimeout;
  }

  /**
   * @return The value of the {@code host} attribute
   */
  @Override
  public String host() {
    return host;
  }

  /**
   * @return The value of the {@code port} attribute
   */
  @Override
  public int port() {
    return port;
  }

  /**
   * @return The value of the {@code ssl} attribute
   */
  @Override
  public boolean ssl() {
    return ssl;
  }

  /**
   * @return The value of the {@code user} attribute
   */
  @Override
  public String user() {
    return user;
  }

  /**
   * @return The value of the {@code password} attribute
   */
  @Override
  public Optional<String> password() {
    return password;
  }

  /**
   * @return The value of the {@code database} attribute
   */
  @Override
  public String database() {
    return database;
  }

  /**
   * @return The value of the {@code connectTimeout} attribute
   */
  @Override
  public DurationParam connectTimeout() {
    return connectTimeout;
  }

  /**
   * @return The value of the {@code socketTimeout} attribute
   */
  @Override
  public DurationParam socketTimeout() {
    return socketTimeout;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MssqlConnectionConfig#host() host} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for host
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withHost(String value) {
    if (this.host.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "host");
    return new ImmutableMssqlConnectionConfig(
        newValue,
        this.port,
        this.ssl,
        this.user,
        this.password,
        this.database,
        this.connectTimeout,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MssqlConnectionConfig#port() port} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for port
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withPort(int value) {
    if (this.port == value) return this;
    return new ImmutableMssqlConnectionConfig(
        this.host,
        value,
        this.ssl,
        this.user,
        this.password,
        this.database,
        this.connectTimeout,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MssqlConnectionConfig#ssl() ssl} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for ssl
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withSsl(boolean value) {
    if (this.ssl == value) return this;
    return new ImmutableMssqlConnectionConfig(
        this.host,
        this.port,
        value,
        this.user,
        this.password,
        this.database,
        this.connectTimeout,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MssqlConnectionConfig#user() user} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for user
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withUser(String value) {
    if (this.user.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "user");
    return new ImmutableMssqlConnectionConfig(
        this.host,
        this.port,
        this.ssl,
        newValue,
        this.password,
        this.database,
        this.connectTimeout,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link MssqlConnectionConfig#password() password} attribute.
   * @param value The value for password
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withPassword(String value) {
    Optional<String> newValue = Optional.of(value);
    if (this.password.equals(newValue)) return this;
    return new ImmutableMssqlConnectionConfig(
        this.host,
        this.port,
        this.ssl,
        this.user,
        newValue,
        this.database,
        this.connectTimeout,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link MssqlConnectionConfig#password() password} attribute.
   * An equality check is used to prevent copying of the same value by returning {@code this}.
   * @param optional A value for password
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withPassword(Optional<String> optional) {
    Optional<String> value = Preconditions.checkNotNull(optional, "password");
    if (this.password.equals(value)) return this;
    return new ImmutableMssqlConnectionConfig(
        this.host,
        this.port,
        this.ssl,
        this.user,
        value,
        this.database,
        this.connectTimeout,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MssqlConnectionConfig#database() database} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for database
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withDatabase(String value) {
    if (this.database.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "database");
    return new ImmutableMssqlConnectionConfig(
        this.host,
        this.port,
        this.ssl,
        this.user,
        this.password,
        newValue,
        this.connectTimeout,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MssqlConnectionConfig#connectTimeout() connectTimeout} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for connectTimeout
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withConnectTimeout(DurationParam value) {
    if (this.connectTimeout == value) return this;
    DurationParam newValue = Preconditions.checkNotNull(value, "connectTimeout");
    return new ImmutableMssqlConnectionConfig(
        this.host,
        this.port,
        this.ssl,
        this.user,
        this.password,
        this.database,
        newValue,
        this.socketTimeout);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MssqlConnectionConfig#socketTimeout() socketTimeout} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for socketTimeout
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMssqlConnectionConfig withSocketTimeout(DurationParam value) {
    if (this.socketTimeout == value) return this;
    DurationParam newValue = Preconditions.checkNotNull(value, "socketTimeout");
    return new ImmutableMssqlConnectionConfig(
        this.host,
        this.port,
        this.ssl,
        this.user,
        this.password,
        this.database,
        this.connectTimeout,
        newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableMssqlConnectionConfig} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableMssqlConnectionConfig
        && equalTo((ImmutableMssqlConnectionConfig) another);
  }

  private boolean equalTo(ImmutableMssqlConnectionConfig another) {
    return host.equals(another.host)
        && port == another.port
        && ssl == another.ssl
        && user.equals(another.user)
        && password.equals(another.password)
        && database.equals(another.database)
        && connectTimeout.equals(another.connectTimeout)
        && socketTimeout.equals(another.socketTimeout);
  }

  /**
   * Computes a hash code from attributes: {@code host}, {@code port}, {@code ssl}, {@code user}, {@code password}, {@code database}, {@code connectTimeout}, {@code socketTimeout}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + host.hashCode();
    h = h * 17 + port;
    h = h * 17 + Booleans.hashCode(ssl);
    h = h * 17 + user.hashCode();
    h = h * 17 + password.hashCode();
    h = h * 17 + database.hashCode();
    h = h * 17 + connectTimeout.hashCode();
    h = h * 17 + socketTimeout.hashCode();
    return h;
  }

  /**
   * Creates an immutable copy of a {@link MssqlConnectionConfig} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable MssqlConnectionConfig instance
   */
  public static ImmutableMssqlConnectionConfig copyOf(MssqlConnectionConfig instance) {
    if (instance instanceof ImmutableMssqlConnectionConfig) {
      return (ImmutableMssqlConnectionConfig) instance;
    }
    return ImmutableMssqlConnectionConfig.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableMssqlConnectionConfig ImmutableMssqlConnectionConfig}.
   * @return A new ImmutableMssqlConnectionConfig builder
   */
  public static ImmutableMssqlConnectionConfig.Builder builder() {
    return new ImmutableMssqlConnectionConfig.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableMssqlConnectionConfig ImmutableMssqlConnectionConfig}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_HOST = 0x1L;
    private static final long INIT_BIT_PORT = 0x2L;
    private static final long INIT_BIT_SSL = 0x4L;
    private static final long INIT_BIT_USER = 0x8L;
    private static final long INIT_BIT_DATABASE = 0x10L;
    private static final long INIT_BIT_CONNECT_TIMEOUT = 0x20L;
    private static final long INIT_BIT_SOCKET_TIMEOUT = 0x40L;
    private long initBits = 0x7fL;

    private @Nullable String host;
    private int port;
    private boolean ssl;
    private @Nullable String user;
    private Optional<String> password = Optional.absent();
    private @Nullable String database;
    private @Nullable DurationParam connectTimeout;
    private @Nullable DurationParam socketTimeout;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code io.digdag.standards.operator.jdbc.AbstractJdbcConnectionConfig} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(AbstractJdbcConnectionConfig instance) {
      Preconditions.checkNotNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    /**
     * Fill a builder with attribute values from the provided {@code com.github.kulmam92.digdag.plugin.mssql.MssqlConnectionConfig} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(MssqlConnectionConfig instance) {
      Preconditions.checkNotNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    private void from(Object object) {
      if (object instanceof AbstractJdbcConnectionConfig) {
        AbstractJdbcConnectionConfig instance = (AbstractJdbcConnectionConfig) object;
        Optional<String> passwordOptional = instance.password();
        if (passwordOptional.isPresent()) {
          password(passwordOptional);
        }
        database(instance.database());
        port(instance.port());
        host(instance.host());
        connectTimeout(instance.connectTimeout());
        socketTimeout(instance.socketTimeout());
        ssl(instance.ssl());
        user(instance.user());
      }
    }

    /**
     * Initializes the value for the {@link MssqlConnectionConfig#host() host} attribute.
     * @param host The value for host 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder host(String host) {
      this.host = Preconditions.checkNotNull(host, "host");
      initBits &= ~INIT_BIT_HOST;
      return this;
    }

    /**
     * Initializes the value for the {@link MssqlConnectionConfig#port() port} attribute.
     * @param port The value for port 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder port(int port) {
      this.port = port;
      initBits &= ~INIT_BIT_PORT;
      return this;
    }

    /**
     * Initializes the value for the {@link MssqlConnectionConfig#ssl() ssl} attribute.
     * @param ssl The value for ssl 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder ssl(boolean ssl) {
      this.ssl = ssl;
      initBits &= ~INIT_BIT_SSL;
      return this;
    }

    /**
     * Initializes the value for the {@link MssqlConnectionConfig#user() user} attribute.
     * @param user The value for user 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder user(String user) {
      this.user = Preconditions.checkNotNull(user, "user");
      initBits &= ~INIT_BIT_USER;
      return this;
    }

    /**
     * Initializes the optional value {@link MssqlConnectionConfig#password() password} to password.
     * @param password The value for password
     * @return {@code this} builder for chained invocation
     */
    public final Builder password(String password) {
      this.password = Optional.of(password);
      return this;
    }

    /**
     * Initializes the optional value {@link MssqlConnectionConfig#password() password} to password.
     * @param password The value for password
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder password(Optional<String> password) {
      this.password = Preconditions.checkNotNull(password, "password");
      return this;
    }

    /**
     * Initializes the value for the {@link MssqlConnectionConfig#database() database} attribute.
     * @param database The value for database 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder database(String database) {
      this.database = Preconditions.checkNotNull(database, "database");
      initBits &= ~INIT_BIT_DATABASE;
      return this;
    }

    /**
     * Initializes the value for the {@link MssqlConnectionConfig#connectTimeout() connectTimeout} attribute.
     * @param connectTimeout The value for connectTimeout 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder connectTimeout(DurationParam connectTimeout) {
      this.connectTimeout = Preconditions.checkNotNull(connectTimeout, "connectTimeout");
      initBits &= ~INIT_BIT_CONNECT_TIMEOUT;
      return this;
    }

    /**
     * Initializes the value for the {@link MssqlConnectionConfig#socketTimeout() socketTimeout} attribute.
     * @param socketTimeout The value for socketTimeout 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder socketTimeout(DurationParam socketTimeout) {
      this.socketTimeout = Preconditions.checkNotNull(socketTimeout, "socketTimeout");
      initBits &= ~INIT_BIT_SOCKET_TIMEOUT;
      return this;
    }

    /**
     * Builds a new {@link ImmutableMssqlConnectionConfig ImmutableMssqlConnectionConfig}.
     * @return An immutable instance of MssqlConnectionConfig
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableMssqlConnectionConfig build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableMssqlConnectionConfig(host, port, ssl, user, password, database, connectTimeout, socketTimeout);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_HOST) != 0) attributes.add("host");
      if ((initBits & INIT_BIT_PORT) != 0) attributes.add("port");
      if ((initBits & INIT_BIT_SSL) != 0) attributes.add("ssl");
      if ((initBits & INIT_BIT_USER) != 0) attributes.add("user");
      if ((initBits & INIT_BIT_DATABASE) != 0) attributes.add("database");
      if ((initBits & INIT_BIT_CONNECT_TIMEOUT) != 0) attributes.add("connectTimeout");
      if ((initBits & INIT_BIT_SOCKET_TIMEOUT) != 0) attributes.add("socketTimeout");
      return "Cannot build MssqlConnectionConfig, some of required attributes are not set " + attributes;
    }
  }
}
