package org.eclipse.cdt.internal.ui.refactoring.encapsulatefield;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;

/**
 * Resolves copyability for C++ Classes
 * 
 * @author Simon Levermann
 * @since 5.9
 */
public class CopyabilityResolver {

	/**
	 * Cache for lookups.
	 */
	private Map<ICPPClassType, Boolean> cache = new HashMap<>();

	/**
	 * Starting point for template instantiation. May be null if classType is not a templated class.
	 */
	private IASTNode startingPoint = null;

	public CopyabilityResolver() {

	}

	public void setStartingPoint(IASTNode startingPoint) {
		this.startingPoint = startingPoint;
	}

	/**
	 * Checks whether a Type is copyable.
	 * 
	 * Checks for both implicit and explicit copyability.
	 * 
	 * @param classType
	 *            the class type to check copyability for
	 * @return true if the type is copyable
	 */
	public boolean isCopyable(ICPPClassType classType) {
		Boolean copyable = cache.get(classType);
		if (copyable != null) {
			if (!copyable) {
				return false;
			}

			if (copyable) {
				return true;
			}
		}

		if (hasExplicitCopyConstructor(classType)) {
			cache.put(classType, true);
			return true;
		}

		if (isExplicitlyUncopyable(classType)) {
			cache.put(classType, false);
			return false;
		}

		if (hasUncopyableBase(classType)) {
			cache.put(classType, false);
			return false;
		}

		if (hasUncopyableFields(classType)) {
			cache.put(classType, false);
			return false;
		}

		cache.put(classType, true);
		return true;
	}

	/**
	 * Check whether a type is explicitly uncopyable.
	 * 
	 * This means one of the following:
	 * <ul>
	 * <li>It has a deleted copy constructor</li>
	 * <li>It has a private copy constructor</li>
	 * <li>It has a protected copy constructor</li>
	 * </ul>
	 * 
	 * Technically, a type may be copyable if the copy constructor is protected, if called from inside the
	 * same class (or a subclass). These types are still assumed to be uncopyable, as getter and setter access
	 * should also work outside of subclasses.
	 * 
	 * @param classType
	 *            the class type to check copyability for
	 * @return true if the type is uncopyable
	 */
	private boolean isExplicitlyUncopyable(ICPPClassType classType) {
		ICPPConstructor[] constructors;
		boolean isTemplate = classType instanceof ICPPClassSpecialization;
		if (isTemplate) {
			constructors = ((ICPPClassSpecialization) classType).getConstructors(startingPoint);
		} else {
			constructors = classType.getConstructors();
		}

		for (ICPPConstructor constructor : constructors) {
			if (constructor.isDeleted() || constructor.getVisibility() == ICPPConstructor.v_private
					|| constructor.getVisibility() == ICPPConstructor.v_protected) {
				if (isCopyConstructor(constructor, classType)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether any base class of this class is uncopyable.
	 * 
	 * @param classType
	 *            the class type to check copyability for
	 * @return true if any base class of this class is uncopyable
	 */
	private boolean hasUncopyableBase(ICPPClassType classType) {
		ICPPBase[] bases;
		if (classType instanceof ICPPClassSpecialization) {
			bases = ((ICPPClassSpecialization) classType).getBases(startingPoint);
		} else {
			bases = classType.getBases();
		}

		for (ICPPBase base : bases) {
			IType baseClass = base.getBaseClassType();
			if (baseClass instanceof ICPPClassType) {
				if (!isCopyable((ICPPClassType) baseClass)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether this type has an explicitly defined copy constructor.
	 * 
	 * @param classType
	 *            the class type to check for
	 * @return true if this type has an explicitly defined copy constructor
	 */
	private boolean hasExplicitCopyConstructor(ICPPClassType classType) {
		ICPPConstructor[] constructors;
		if (classType instanceof ICPPClassSpecialization) {
			constructors = ((ICPPClassSpecialization) classType).getConstructors(startingPoint);
		} else {
			constructors = classType.getConstructors();
		}

		for (ICPPConstructor constructor : constructors) {
			if (constructor.isExplicit()) {
				if (isCopyConstructor(constructor, classType)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether this constructor is a copy constructor inside the class.
	 * 
	 * @param constructor
	 *            the constructor
	 * @param classType
	 *            the class
	 * @return true if the given constructor is a copy constructor inside the given class.
	 */
	private boolean isCopyConstructor(ICPPConstructor constructor, ICPPClassType classType) {
		ICPPParameter[] params = constructor.getParameters();
		if (params.length != 1) {
			return false;
		}

		ICPPParameter param = params[0];
		IType type = param.getType();
		if (type instanceof ICPPReferenceType) {
			type = ((ICPPReferenceType) type).getType();
		} else {
			return false;
		}
		if (type instanceof IQualifierType) {
			if (!((IQualifierType) type).isConst()) {
				return false;
			}
			return ((IQualifierType) type).getType().isSameType(classType);
		} else {
			return false;
		}
	}

	/**
	 * Check whether this class type has fields that are of uncopyable types
	 * 
	 * @param classType
	 *            the class to check for
	 * @return true if the class has fields that are uncopiable
	 */
	private boolean hasUncopyableFields(ICPPClassType classType) {
		/*
		 * We only traverse declared (not inherited) fields. This method is only invoked after base classes
		 * are already checked, so declared fields of base classes make those classes uncopyable, and this
		 * method will not even be invoked.
		 */
		ICPPField[] fields;
		if (classType instanceof ICPPClassSpecialization) {
			fields = ((ICPPClassSpecialization) classType).getDeclaredFields(startingPoint);
		} else {
			fields = classType.getDeclaredFields();
		}

		for (ICPPField field : fields) {
			IType type = field.getType();
			if (type instanceof ICPPClassType) {
				ICPPClassType fieldClassType = (ICPPClassType) type;
				if (!isCopyable(fieldClassType)) {
					return true;
				}
			}
		}
		return false;
	}
}
