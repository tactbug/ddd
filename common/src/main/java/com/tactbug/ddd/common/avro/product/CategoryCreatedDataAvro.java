/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.tactbug.ddd.common.avro.product;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class CategoryCreatedDataAvro extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 736903905971653593L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CategoryCreatedDataAvro\",\"namespace\":\"com.tactbug.ddd.common.avro.product\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"remark\",\"type\":\"string\"},{\"name\":\"parentId\",\"type\":\"long\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<CategoryCreatedDataAvro> ENCODER =
      new BinaryMessageEncoder<CategoryCreatedDataAvro>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<CategoryCreatedDataAvro> DECODER =
      new BinaryMessageDecoder<CategoryCreatedDataAvro>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<CategoryCreatedDataAvro> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<CategoryCreatedDataAvro> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<CategoryCreatedDataAvro> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<CategoryCreatedDataAvro>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this CategoryCreatedDataAvro to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a CategoryCreatedDataAvro from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a CategoryCreatedDataAvro instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static CategoryCreatedDataAvro fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence name;
  private java.lang.CharSequence remark;
  private long parentId;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public CategoryCreatedDataAvro() {}

  /**
   * All-args constructor.
   * @param name The new value for name
   * @param remark The new value for remark
   * @param parentId The new value for parentId
   */
  public CategoryCreatedDataAvro(java.lang.CharSequence name, java.lang.CharSequence remark, java.lang.Long parentId) {
    this.name = name;
    this.remark = remark;
    this.parentId = parentId;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return name;
    case 1: return remark;
    case 2: return parentId;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: name = (java.lang.CharSequence)value$; break;
    case 1: remark = (java.lang.CharSequence)value$; break;
    case 2: parentId = (java.lang.Long)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.CharSequence getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'remark' field.
   * @return The value of the 'remark' field.
   */
  public java.lang.CharSequence getRemark() {
    return remark;
  }


  /**
   * Sets the value of the 'remark' field.
   * @param value the value to set.
   */
  public void setRemark(java.lang.CharSequence value) {
    this.remark = value;
  }

  /**
   * Gets the value of the 'parentId' field.
   * @return The value of the 'parentId' field.
   */
  public long getParentId() {
    return parentId;
  }


  /**
   * Sets the value of the 'parentId' field.
   * @param value the value to set.
   */
  public void setParentId(long value) {
    this.parentId = value;
  }

  /**
   * Creates a new CategoryCreatedDataAvro RecordBuilder.
   * @return A new CategoryCreatedDataAvro RecordBuilder
   */
  public static com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder newBuilder() {
    return new com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder();
  }

  /**
   * Creates a new CategoryCreatedDataAvro RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new CategoryCreatedDataAvro RecordBuilder
   */
  public static com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder newBuilder(com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder other) {
    if (other == null) {
      return new com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder();
    } else {
      return new com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder(other);
    }
  }

  /**
   * Creates a new CategoryCreatedDataAvro RecordBuilder by copying an existing CategoryCreatedDataAvro instance.
   * @param other The existing instance to copy.
   * @return A new CategoryCreatedDataAvro RecordBuilder
   */
  public static com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder newBuilder(com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro other) {
    if (other == null) {
      return new com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder();
    } else {
      return new com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder(other);
    }
  }

  /**
   * RecordBuilder for CategoryCreatedDataAvro instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CategoryCreatedDataAvro>
    implements org.apache.avro.data.RecordBuilder<CategoryCreatedDataAvro> {

    private java.lang.CharSequence name;
    private java.lang.CharSequence remark;
    private long parentId;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.remark)) {
        this.remark = data().deepCopy(fields()[1].schema(), other.remark);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.parentId)) {
        this.parentId = data().deepCopy(fields()[2].schema(), other.parentId);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing CategoryCreatedDataAvro instance
     * @param other The existing instance to copy.
     */
    private Builder(com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.remark)) {
        this.remark = data().deepCopy(fields()[1].schema(), other.remark);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.parentId)) {
        this.parentId = data().deepCopy(fields()[2].schema(), other.parentId);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.CharSequence getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder setName(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.name = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder clearName() {
      name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'remark' field.
      * @return The value.
      */
    public java.lang.CharSequence getRemark() {
      return remark;
    }


    /**
      * Sets the value of the 'remark' field.
      * @param value The value of 'remark'.
      * @return This builder.
      */
    public com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder setRemark(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.remark = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'remark' field has been set.
      * @return True if the 'remark' field has been set, false otherwise.
      */
    public boolean hasRemark() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'remark' field.
      * @return This builder.
      */
    public com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder clearRemark() {
      remark = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'parentId' field.
      * @return The value.
      */
    public long getParentId() {
      return parentId;
    }


    /**
      * Sets the value of the 'parentId' field.
      * @param value The value of 'parentId'.
      * @return This builder.
      */
    public com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder setParentId(long value) {
      validate(fields()[2], value);
      this.parentId = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'parentId' field has been set.
      * @return True if the 'parentId' field has been set, false otherwise.
      */
    public boolean hasParentId() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'parentId' field.
      * @return This builder.
      */
    public com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro.Builder clearParentId() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CategoryCreatedDataAvro build() {
      try {
        CategoryCreatedDataAvro record = new CategoryCreatedDataAvro();
        record.name = fieldSetFlags()[0] ? this.name : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.remark = fieldSetFlags()[1] ? this.remark : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.parentId = fieldSetFlags()[2] ? this.parentId : (java.lang.Long) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<CategoryCreatedDataAvro>
    WRITER$ = (org.apache.avro.io.DatumWriter<CategoryCreatedDataAvro>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<CategoryCreatedDataAvro>
    READER$ = (org.apache.avro.io.DatumReader<CategoryCreatedDataAvro>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.name);

    out.writeString(this.remark);

    out.writeLong(this.parentId);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);

      this.remark = in.readString(this.remark instanceof Utf8 ? (Utf8)this.remark : null);

      this.parentId = in.readLong();

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
          break;

        case 1:
          this.remark = in.readString(this.remark instanceof Utf8 ? (Utf8)this.remark : null);
          break;

        case 2:
          this.parentId = in.readLong();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










